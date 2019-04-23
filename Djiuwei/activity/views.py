from django.shortcuts import get_object_or_404
from django.http import JsonResponse
from django.urls import reverse
from django.views.generic import View
from activity.models import Activity, UserActivityMap, Dynamic
import json
from time import mktime, time
<<<<<<< HEAD
from datetime import  datetime
=======
>>>>>>> 77b7481a1b00a05546a5f8e94f8833ffa46d5881


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
<<<<<<< HEAD
        print(request.COOKIES)
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if not isinstance(request_msg, dict):
=======
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if isinstance(request_msg, dict):
>>>>>>> 77b7481a1b00a05546a5f8e94f8833ffa46d5881
                return JsonResponse({"msg": "typeErr_dict"})
        except Exception as e:
            print(e)
            request_msg = {}

        user = request.user
<<<<<<< HEAD
        print(user)
        activity = Activity()
        activity.activity_name = request_msg.get("activity_name", "")
        activity.activity_desc = request_msg.get("activity_desc", "")
        activity_time = request_msg.get("activity_time", "")
        ymd, hms = activity_time.split(" ")
        year, month, day = list(map(int, ymd.split("-")))
        hour, minute = list(map(int, hms.split(":")))
        activity.activity_time = datetime(year, month, day, hour, minute, 0)
=======
        activity = Activity()
        activity.activity_name = request_msg.get("activity_name", "")
        activity.activity_desc = request_msg.get("activity_desc", "")
        activity_time = request_msg.get("activity_time", "").split("-")
        if len(activity_time) != 3:
            return JsonResponse({"msg": "timeErr"})
        year, month, day = list[map(int, activity_time.split("-"))]
>>>>>>> 77b7481a1b00a05546a5f8e94f8833ffa46d5881
        activity.activity_site = request_msg.get("activity_site", "")
        activity.limit_num = request_msg.get("limit_num", 10)
        # activity.limit_requirement = request_msg.get("limit_requirement", "")
        activity.activity_type = request_msg.get("activity_type", "")
        activity.owner_id = user.id

        # 各字段缺失情况处理
        if not all([activity.activity_name, activity.activity_desc, activity.activity_time, activity.activity_site,
                    activity.limit_num, activity.owner_id, activity.activity_type]):
            return JsonResponse({"msg": "fieldErr_lose"})

        # 数据入库
        activity.save()

        # 创建用户和活动关系映射
        uamap = UserActivityMap()
        uamap.user_id = request.user.id
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
        return JsonResponse({"changeActivity": 1})


class MyActivity(View):
    """我的活动视图"""

    def post(self, request):
<<<<<<< HEAD
        return JsonResponse({"myActivity": 1})
=======
        return JsonResponse({"myActivity": 1})
>>>>>>> 77b7481a1b00a05546a5f8e94f8833ffa46d5881
