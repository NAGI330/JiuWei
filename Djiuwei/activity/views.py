from django.shortcuts import get_object_or_404
from django.http import JsonResponse
from django.urls import reverse
from django.views.generic import View
from activity.models import Activity
import json
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
			if isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			print(e)
			request_msg = {}

		# test
		print("user status: {}".format(request.user))

		activity = Activity()
		activity.activity_name = request_msg.get("activity_name", "")
		activity.activity_desc = request_msg.get("activity_desc", "")
		activity.activity_time = request_msg.get("activity_time", "")
		activity.activity_site = request_msg.get("activity_site", "")
		activity.limit_num = request_msg.get("limit_num", 0)
		# activity.owner_id = request_msg.get("owner_id", 0)
		# activity.limit_requirement = request_msg.get("limit_requirement", "")
		activity.activity_type = request_msg.get("activity_type", "")

		# 各字段缺失情况处理
		if not all([activity.activity_name, activity.activity_desc, activity.activity_time, activity.activity_site, activity.limit_num, activity.limit_requirement, activity.owner_id, activity.activity_type]):
			return JsonResponse({"msg": "fieldErr_lose"})

		# 数据入库
		activity.save()
		return JsonResponse({"msg": "createActivity Successfully"})


class ChangeActivity(View):
	"""修改活动视图"""
	def post(self, request):
		"""修改活动"""
		return JsonResponse({"changeActivity": 1})


class MyActivity(View):
	"""我的活动视图"""
	def post(self, request):
		return JsonResponse({"myActivity": 1})
