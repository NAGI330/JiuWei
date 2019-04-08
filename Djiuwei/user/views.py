from django.shortcuts import render
from django.views.generic import View
import json
from django.http import JsonResponse
# from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
# from itsdangerous import SignatureExpired
# from celery_tasks.tasks import send_register_active_mail

# Create your views here.

class SignOn(View):
	"""注册视图"""
	def post(self, request):
		"""注册校验"""
		return JsonResponse({"signOn": 1})


class ActiveView(View):
	"""邮箱激活"""
	def get(self, request, token):
		"""进行用户激活"""
		return JsonResponse({"is_active": 1})


class SignIn(View):
	"""登录视图"""
	def post(self, request):
		"""登录校验"""
		return JsonResponse({"signIn": 1})


class ChangeMsg(View):
	"""修改个人信息视图"""
	def post(self, request):
		return JsonResponse({"changMsg": 1})
