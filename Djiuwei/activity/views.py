from django.shortcuts import get_object_or_404
from django.http import JsonResponse
from django.urls import reverse
from django.views.generic import View
from activity.models import Activity, UserActivityMap
import json
from utils.user_status import checkStatus
from user.models import User
from datetime import datetime
from time import mktime
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from haystack.query import SearchQuerySet
from haystack.inputs import AutoQuery
from random import randint
# Create your views here.


class PushHot(View):
	"""推送热点视图"""
	def post(self, request):
		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})
		# 筛选出活动时间早于当前的
		now = datetime.now()
		count = Activity.objects.filter(activity_time__gt=now).exclude(owner_id=user.id).count()
		start = randint(0, count - 4)
		start = start if start > 0 else 0
		activities = Activity.objects.filter(activity_time__gt=now).exclude(owner_id=user.id)[start:start+4]

		# response 拼接
		response = dict()
		for activity in activities:
			if activity.useractivitymap_set.all().count() > activity.limit_num:
				continue
			activity_name = activity.activity_name
			activity_site = activity.activity_site
			activity_type = activity.activity_type
			activity_desc = activity.activity_desc
			activity_time = activity.activity_time
			owner_id = activity.owner_id
			owner = User.objects.get(id=owner_id)
			owner_nickname = owner.nickname			
			response[activity.id] = {"activity_name": activity_name, "activity_desc": activity_desc, "activity_time": activity_time, "activity_site": activity_site, "activity_type": activity_type, "owner_id": owner_id, "owner_nickname": owner_nickname}

		return JsonResponse({"response": response})


class CreateActivity(View):
	"""创建活动"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})

		activity = Activity()
		activity.activity_name = request_msg.get("activity_name", "")
		activity.activity_desc = request_msg.get("activity_desc", "")
		activity_time = request_msg.get("activity_time", "").strip()
		ymd, hms = activity_time.split(" ")
		year, month, day = list(map(int, ymd.split("-")))
		hour, minute = list(map(int, hms.split(":")))
		activity.activity_time = datetime(year, month, day, hour, minute, 0)
		activity.activity_site = request_msg.get("activity_site", "")
		activity.limit_num = request_msg.get("limit_num", 0)
		activity.owner_id = user.id
		# activity.limit_requirement = request_msg.get("limit_requirement", "")
		activity.activity_type = request_msg.get("activity_type", "")

		# 各字段缺失情况处理
		if not all([activity.activity_name, activity.activity_desc, activity.activity_time, activity.activity_site, activity.limit_num, activity.owner_id, activity.activity_type]):
			return JsonResponse({"msg": "fieldErr_lose"})

		# 数据入库
		activity.save()

		# 创建用户和活动关系映射
		uamap = UserActivityMap()
		uamap.user_id = user.id
		uamap.activity_id = activity.id
		uamap.save()

		return JsonResponse({"msg": "createActivity Successfully"})


class ChangeActivity(View):
	"""修改活动视图"""
	def post(self, request):
		"""修改活动"""
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})
		
		activity_id = request_msg.get("id", 0)
		try:
			activity = Activity.objects.get(id=activity_id)
		except Exception as e:
			return JsonResponse({"msg": "activityErr_id"})

<<<<<<< HEAD
class MineActivity(View):
    """我的活动视图"""
    def post(self, request):
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if not isinstance(request_msg, dict):
                return JsonResponse({"msg": "typeErr_dict"})
        except Exception as e:
            print(e)
            request_msg = {}

        # 用户登录状态校验
        user = checkStatus(request.COOKIES)
        if not isinstance(user, User):
            return JsonResponse({"msg": "userErr_unsignIn"})
        object_list = Activity.objects.filter(owner_id=user.id).order_by("-activity_time")

        # 将全部结果分页
        paginator = Paginator(object_list, 4)
        page = int(request_msg.get("page"))
        print("page: {}".format(page))
        try:
            activities = paginator.page(page)
        except PageNotAnInteger:
            activities = paginator.page(1)
        except EmptyPage:
            return JsonResponse({"msg": "numErr_Empty"})

        # response 拼接
        response = {
        i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time,
               "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num,
               "owner_id": i.owner_id, "owner_name": user.nickname, "participant": []} for i in activities}
        users_id = [i.user_id for activity in activities for i in activity.useractivitymap_set.all()]
        users_nickname = [i.nickname for id in users_id for i in User.objects.filter(id=id)]
        for i in activities:
            response[i.id]["participant"] = [{"id": id, "name": name} for id in users_id for name in users_nickname if
                                             i.owner_id != id]
            response[i.id]["participant_num"] = len(users_id)

        print("response: {}".format(response))

        return JsonResponse({"activities": response, "msg": "paginatorSuc"})
=======
		activity.activity_name = request_msg.get("activity_name", "")
		activity.activity_desc = request_msg.get("activity_desc", "")
		activity_time = request_msg.get("activity_time", "").strip()
		ymd, hms = activity_time.split(" ")
		year, month, day = list(map(int, ymd.split("-")))
		hour, *minutes = list(map(int, hms.split(":")))
		if len(minutes) == 2:
			minute, second = minutes
		else:
			minute = minutes[0]
		activity.activity_time = datetime(year, month, day, hour, minute, 0)
		activity.activity_site = request_msg.get("activity_site", "")
		activity.limit_num = request_msg.get("limit_num", 0)
>>>>>>> e9a08a745b1e71dad963f74f2551b65cd7b52d85

		# 数据入库
		activity.save()

<<<<<<< HEAD
class HistoryActivity(View):
    """历史活动视图"""

    def post(self, request):
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if not isinstance(request_msg, dict):
                return JsonResponse({"msg": "typeErr_dict"})
        except Exception as e:
            print(e)
            request_msg = {}

        # 用户登录状态校验
        user = checkStatus(request.COOKIES)
        if not isinstance(user, User):
            return JsonResponse({"msg": "userErr_unsignIn"})

        # 查询该用户相关的所有活动
        activities = [map.activity_id for map in UserActivityMap.objects.filter(user_id=user.id)]

        # 筛选出活动时间早于当前的
        now = datetime.now()
        object_list = Activity.objects.filter(owner_id=user.id, activity_time__lt=now).filter(id__in=activities)

        # 将全部结果分页
        paginator = Paginator(object_list, 4)
        page = int(request_msg.get("page"))
        print("page: {}".format(page))
        try:
            activities = paginator.page(page)
        except PageNotAnInteger:
            activities = paginator.page(1)
        except EmptyPage:
            return JsonResponse({"msg": "numErr_Empty"})

        # response 拼接
        response = {
        i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time,
               "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num,
               "owner_id": i.owner_id, "owner_name": user.nickname, "participant": []} for i in activities}
        users_id = [i.user_id for activity in activities for i in activity.useractivitymap_set.all()]
        users_nickname = [i.nickname for id in users_id for i in User.objects.filter(id=id)]
        for i in activities:
            response[i.id]["participant"] = [{"id": id, "name": name} for id in users_id for name in users_nickname if
                                             i.owner_id != id]
            response[i.id]["participant_num"] = len(users_id)

        print("response: {}".format(response))

        return JsonResponse({"activities": response, "msg": "paginatorSuc"})


class ToJoinActivity(View):
    """待参加活动视图"""

    def post(self, request):
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if not isinstance(request_msg, dict):
                return JsonResponse({"msg": "typeErr_dict"})
        except Exception as e:
            print(e)
            request_msg = {}

        # 用户登录状态校验
        user = checkStatus(request.COOKIES)
        if not isinstance(user, User):
            return JsonResponse({"msg": "userErr_unsignIn"})

        # 查询该用户相关的所有活动
        activities = [map.activity_id for map in UserActivityMap.objects.filter(user_id=user)]

        # 筛选出活动时间早于当前的
        now = datetime.now()
        object_list = Activity.objects.filter(owner_id=user.id, activity_time__gt=now).filter(id__in=activities)

        # 将全部结果分页
        paginator = Paginator(object_list, 4)
        page = int(request_msg.get("page"))
        print("page: {}".format(page))
        try:
            activities = paginator.page(page)
        except PageNotAnInteger:
            activities = paginator.page(1)
        except EmptyPage:
            return JsonResponse({"msg": "numErr_Empty"})

        # response 拼接
        response = {
        i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time,
               "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num,
               "owner_id": i.owner_id, "owner_name": user.nickname, "participant": []} for i in activities}
        users_id = [i.user_id for activity in activities for i in activity.useractivitymap_set.all()]
        print(users_id)
        users_nickname = [i.nickname for id in users_id for i in User.objects.filter(id=id)]
        for i in activities:
            response[i.id]["participant"] = [{"id": id, "name": name} for id in users_id for name in users_nickname if
                                             i.owner_id != id]
            response[i.id]["participant_num"] = len(users_id)

        print("response: {}".format(response))

        return JsonResponse({"activities": response, "msg": "paginatorSuc"})


class QuitActivity(View):
    """退出活动"""

    def post(self, request):
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if not isinstance(request_msg, dict):
                return JsonResponse({"msg": "typeErr_dict"})
        except Exception as e:
            print(e)
            request_msg = {}

        # 用户登录状态校验
        user = checkStatus(request.COOKIES)
        if not isinstance(user, User):
            return JsonResponse({"msg": "userErr_unsignIn"})

        activity_id = request_msg.get("id", 0)
        try:
            activity = Activity.objects.get(id=activity_id)
        except Exception as e:
            print(e)
            return JsonResponse({"msg": "activityErr_id"})

        try:
            uamap = UserActivityMap.objects.get(user_id=user.id, activity_id=activity.id)
        except Exception as e:
            return JsonResponse({"msg": "uamapErr_notExist"})
        uamap.delete()

        user.credit_score -= 5
        if user.id != activity.owner_id:
            score = user.credit_score
            user.save()
            return JsonResponse({"msg": "quit successfully", "credit_score": score})

        activity.delete()
        user.credit_score -= 5
        score = user.credit_score
        return JsonResponse({"msg": "dissolution successfully", "credit_score": score})
=======
		return JsonResponse({"msg": "change activity successfully"})


def response_construct(activities, user):
	"""response 拼接"""
	response = {i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time, "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num, "owner_id": i.owner_id, "owner_name": User.objects.get(id=i.owner_id).nickname, "participant": []} for i in activities}
	users_id = {i.user_id for activity in activities for i in activity.useractivitymap_set.all()}
	# 添加字段
	for i in activities:
		response[i.id]["participant"] = [{id: User.objects.get(id=id).nickname} for id in users_id]
		response[i.id]["participant_num"] = len(users_id)
		response[i.id]["activity_isMine"] = 1 if user.id == i.owner_id else 0
		response[i.id]["activity_status"] = 1 if i.activity_time.timestamp() >= datetime.now().timestamp() else 0

	return response


class MineActivity(View):
	"""我的活动视图"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})
		object_list = Activity.objects.filter(owner_id=user.id).order_by("-activity_time")

		# 将全部结果分页
		paginator = Paginator(object_list, 4)
		page = int(request_msg.get("page"))
		try:
			activities = paginator.page(page)
		except PageNotAnInteger:
			activities = paginator.page(1)
		except EmptyPage:
			return JsonResponse({"msg": "numErr_Empty"})
		
		# response 拼接
		response = response_construct(activities, user)
		
		return JsonResponse({"activities": response, "msg": "paginatorSuc"})


class HistoryActivity(View):
	"""历史活动视图"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})

		# 查询该用户相关的所有活动
		activities = [map.activity_id for map in UserActivityMap.objects.filter(user_id=user.id)]

		# 筛选出活动时间早于当前的
		now = datetime.now()
		object_list = Activity.objects.filter(activity_time__lt=now).filter(id__in=activities)

		# 将全部结果分页
		paginator = Paginator(object_list, 4)
		page = int(request_msg.get("page"))
		try:
			activities = paginator.page(page)
		except PageNotAnInteger:
			activities = paginator.page(1)
		except EmptyPage:
			return JsonResponse({"msg": "numErr_Empty"})
		
		# response 拼接
		response = response_construct(activities, user)

		return JsonResponse({"activities": response, "msg": "paginatorSuc"})


class TojoinActivity(View):
	"""待参加活动视图"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})

		# 查询该用户相关的所有活动
		activities = [map.activity_id for map in UserActivityMap.objects.filter(user_id=user)]

		# 筛选出活动时间早于当前的
		now = datetime.now()
		object_list = Activity.objects.filter(activity_time__gt=now).filter(id__in=activities).order_by("activity_time")

		# 将全部结果分页
		paginator = Paginator(object_list, 4)
		page = int(request_msg.get("page"))
		try:
			activities = paginator.page(page)
		except PageNotAnInteger:
			activities = paginator.page(1)
		except EmptyPage:
			return JsonResponse({"msg": "numErr_Empty"})
		
		# response 拼接
		response = response_construct(activities, user)

		return JsonResponse({"activities": response, "msg": "paginatorSuc"})


class QuitActivity(View):
	"""退出活动"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})

		activity_id = request_msg.get("id", 0)
		try:
			activity = Activity.objects.get(id=activity_id)
		except Exception as e:
			return JsonResponse({"msg": "activityErr_id"})

		try:
			uamap = UserActivityMap.objects.get(user_id=user.id, activity_id=activity.id)
		except Exception as e:
			return JsonResponse({"msg": "uamapErr_notExist"})
		uamap.delete()

		user.credit_score -= 5
		if user.id != activity.owner_id:
			score = user.credit_score
			user.save()
			return JsonResponse({"msg": "quit successfully", "credit_score": score})

		activity.delete()
		user.credit_score -= 5
		score = user.credit_score
		return JsonResponse({"msg": "dissolution successfully", "credit_score": score})


class searchView(View):
	"""自定义搜索视图"""
	def get(self, request, q):
		query = str(q)
		# results = SearchQuerySet().models(Activity).filter(content=query).load_all()
		results = SearchQuerySet().filter(content=AutoQuery(query))
		if not results:
			return JsonResponse({"response": "query null"})
		response = dict()
		for result in results:
			try:
				id = int(result.object.id)
				activity = Activity.objects.get(id=id)
				if activity.useractivitymap_set.all().count() > activity.limit_num:
					continue
				activity_name = activity.activity_name
				activity_site = activity.activity_site
				activity_type = activity.activity_type
				activity_desc = activity.activity_desc
				activity_time = activity.activity_time
				owner_id = activity.owner_id
				owner = User.objects.get(id=owner_id)
				owner_nickname = owner.nickname
				response[id] = {"activity_name": activity_name, "activity_desc": activity_desc, "activity_time": activity_time, "activity_site": activity_site, "activity_type": activity_type, "owner_id": owner_id, "owner_nickname": owner_nickname}
			except Exception as e:
				return JsonResponse({"response": "query null"})
		if not response:
			return JsonResponse({"response": "query null"})

		return JsonResponse({"response": response})


class joinInActivity(View):
	"""加入活动"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 用户登录状态校验
		user = checkStatus(request.COOKIES)
		if not isinstance(user, User):
			return JsonResponse({"msg": "userErr_unsignIn"})

		uamap = UserActivityMap()
		activity_id = int(request_msg.get("activity_id", 0))
		if not activity_id:
			return JsonResponse({"msg": "activityErr_notExist"})
		uamap.activity_id = activity_id
		uamap.user_id = user.id
		uamap.save()
		return JsonResponse({"msg": "join successfully"})

>>>>>>> e9a08a745b1e71dad963f74f2551b65cd7b52d85
