from django.shortcuts import get_object_or_404
from django.http import JsonResponse
from django.urls import reverse
from django.views.generic import View
from activity.models import Activity, UserActivityMap, Dynamic
import json
from utils.user_status import checkStatus
from user.models import User
from datetime import datetime
from time import mktime
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
# Create your views here.


class PushFriend(View):
	"""推送好友活动视图"""
	def post(self, request):
		"""推送好友活动"""
		return JsonResponse({"push friends' activities": 1})


class PushHot(View):
	"""推送热点视图"""
	def post(self, request):
		return JsonResponse({"push hot activities": 1})


class CreateActivity(View):
	"""创建活动"""
	def get(self, request):
		return JsonResponse({"createActivity": 1})

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

		# 创建动态
		dynamic = Dynamic()
		dynamic.activity_id = activity.id
		dynamic.save()

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

		print(request_msg)
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

		# 数据入库
		activity.save()

		return JsonResponse({"msg": "change activity successfully"})


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
		response = {i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time, "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num, "owner_id": i.owner_id, "owner_name": user.nickname, "participant": []} for i in activities}
		users_id = [i.user_id for activity in activities for i in activity.useractivitymap_set.all()]
		users_nickname = [i.nickname for id in users_id for i in User.objects.filter(id=id)]
		for i in activities:
			response[i.id]["participant"] = [{"id": id, "name": name} for id in users_id for name in users_nickname if i.owner_id != id]
			response[i.id]["participant_num"] = len(users_id)

		print("response: {}".format(response))
		
		return JsonResponse({"activities": response})


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
		response = {i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time, "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num, "owner_id": i.owner_id, "owner_name": user.nickname, "participant": []} for i in activities}
		users_id = [i.user_id for activity in activities for i in activity.useractivitymap_set.all()]
		users_nickname = [i.nickname for id in users_id for i in User.objects.filter(id=id)]
		for i in activities:
			response[i.id]["participant"] = [{"id": id, "name": name} for id in users_id for name in users_nickname if i.owner_id != id]
			response[i.id]["participant_num"] = len(users_id)
		
		print("response: {}".format(response))

		return JsonResponse({"activities": response})


class TojoinActivity(View):
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
		response = {i.id: {"activity_name": i.activity_name, "activity_desc": i.activity_desc, "activity_time": i.activity_time, "activity_site": i.activity_site, "activity_type": i.activity_type, "limit_num": i.limit_num, "owner_id": i.owner_id, "owner_name": user.nickname, "participant": []} for i in activities}
		users_id = [i.user_id for activity in activities for i in activity.useractivitymap_set.all()]
		users_nickname = [i.nickname for id in users_id for i in User.objects.filter(id=id)]
		for i in activities:
			response[i.id]["participant"] = [{"id": id, "name": name} for id in users_id for name in users_nickname if i.owner_id != id]
			response[i.id]["participant_num"] = len(users_id)
		
		print("response: {}".format(response))

		return JsonResponse({"activities": response})


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

