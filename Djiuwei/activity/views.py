from django.shortcuts import get_object_or_404
from django.http import JsonResponse
from django.urls import reverse
from django.views.generic import View
from activity.models import Activity, UserActivityMap, Dynamic
import json
from utils.user_status import checkStatus
from user.models import User
from datetime import  datetime
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
        print(request.COOKIES)
        try:
            # 请求格式错误处理
            request_msg = json.loads(request.body)
            if not isinstance(request_msg, dict):
                return JsonResponse({"msg": "typeErr_dict"})
        except Exception as e:
            print(e)
            request_msg = {}

        user = request.user
        print(user)
        activity = Activity()
        activity.activity_name = request_msg.get("activity_name", "")
        activity.activity_desc = request_msg.get("activity_desc", "")
        activity_time = request_msg.get("activity_time", "")
        ymd, hms = activity_time.split(" ")
        year, month, day = list(map(int, ymd.split("-")))
        hour, minute = list(map(int, hms.split(":")))
        activity.activity_time = datetime(year, month, day, hour, minute, 0)
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
        #return JsonResponse({"activities": "abc"})
        return JsonResponse({"activities":{"5":{"activity_name":"天气真好","activity_time":"2018-09-26","activity_type":"唱歌","limit_num":"4"},
                                           "6":{"activity_name":"天气一般","activity_time":"2018-03-23","activity_type":"逛街","limit_num":"3"},
                                           "7":{"activity_name":"天气很差","activity_time":"2019-01-26","activity_type":"吃饭","limit_num":"9"},
                                           "3":{"activity_name":"出去玩吧","activity_time":"2019-04-11","activity_type":"爬山","limit_num":"6"}} })

class HistoryActivity(View):
    """我的活动视图"""

    def post(self, request):
        #return JsonResponse({"activities": "abc"})
        return JsonResponse({"activities":{"35":{"activity_name":"每日坚果","activity_time":"2018-04-26","activity_type":"唱歌","limit_num":"4"},
                                           "46":{"activity_name":"特仑苏","activity_time":"2018-04-06","activity_type":"唱歌","limit_num":"3"},
                                           "72":{"activity_name":"羊肉泡馍","activity_time":"2017-02-26","activity_type":"唱歌","limit_num":"9"},
                                           "33":{"activity_name":"活动名","activity_time":"2014-03-11","activity_type":"唱歌","limit_num":"6"}} })

class ToJoinActivity(View):
    """我的活动视图"""

    def post(self, request):
        #return JsonResponse({"activities": "abc"})
        return JsonResponse({"activities":{"21":{"activity_name":"春","activity_time":"2018-12-26","activity_type":"跑步","limit_num":"14"},
                                           "22":{"activity_name":"夏","activity_time":"2018-08-06","activity_type":"跑步","limit_num":"13"},
                                           "23":{"activity_name":"秋","activity_time":"2017-04-26","activity_type":"跑步","limit_num":"19"},
                                           "24":{"activity_name":"冬","activity_time":"2019-11-11","activity_type":"跑步","limit_num":"16"}} })

