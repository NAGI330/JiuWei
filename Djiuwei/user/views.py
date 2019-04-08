from django.shortcuts import get_object_or_404
from django.views.generic import View
import json
from django.http import JsonResponse
from user.models import User
from django.core.mail import send_mail
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from itsdangerous import SignatureExpired
from django.conf import settings
from django.contrib.auth import authenticate, login
# from celery_tasks.tasks import send_register_active_mail

# Create your views here.

class SignOnView(View):
	"""注册视图"""
	def post(self, request):
		"""注册校验"""
		# 1.请求数据校验
		try:
			request_msg = json.loads(request.body)
			if isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			print(e)
			request_msg = {}

		# 2.接受数据
		username = request_msg.get("username", "")
		password = request_msg.get("password", "")
		email = request_msg.get("email", "")

		# 3.校验数据完整性
		if not all([username, password, email]):
			return JsonResponse({"msg": "fieldErr_lose"})

		# 4.检测用户是否已存在
		try:
			user = User.objects.get(username=username)
		except User.DoesNotExist:
			user = None
		if user:
			return JsonResponse({"msg": "userErr_exist"})

		# 进行用户注册
		user = User.objects.create_user(username, email, password)
		user.is_active = 0
		user.save()

		# 发送激活邮件(为测试使用先阻塞处理)
		# 加密
		serializer = Serializer(settings.SECRET_KEY, 3600)
		info = {"confirm": user.id}
		token = serializer.dumps(info)
		token = token.decode("utf-8")
		
		# 邮件信息
		subject = "久违欢迎你!!!"
		message = ""
		sender = settings.EMAIL_HOST_USER
		receiver = [email]
		html_message = "<h1>尊敬的{}, 欢迎您注册成为久违的用户</h1>请点击下面的链接激活您的账户<br /><a href='http://192.168.21.128:8000/user/Active/{}'>http://192.168.21.128/user/Active/{}</a>".format(username, token, token)
		send_mail(subject, message, sender, receiver, html_message=html_message)

		return JsonResponse({"msg": "signOn successfully"})


class ActiveView(View):
	"""邮箱激活"""
	def get(self, request, token):
		"""进行用户激活"""
		# 解密
		serializer = Serializer(settings.SECRET_KEY, 3600)
		try:
			info = serializer.loads(token)
			user_id = info['confirm']
			user = User.objects.get(id=user_id)
			user.is_active = 1
			user.save()
			return JsonResponse({"msg": "user is active"})
		except SignatureExpired as e:
			print(e)
			return JsonResponse({"msg": "active is out of time"})


class SignInView(View):
	"""登录视图"""
	def post(self, request):
		"""登录校验"""
		# 1.请求数据校验
		try:
			request_msg = json.loads(request.body)
			if isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			print(e)
			request_msg = {}

		# 2.接受数据
		username = request_msg.get("username", "")
		password = request_msg.get("password", "")

		# 3.校验数据
		if not all([username, password]):
			return JsonResponse({"msg": "fieldErr_lose"})

		# 4.登录验证
		user = authenticate(username=username, password=password)
		if user is not None:
			if user.is_active:
				# 用户id存入session
				login(request, user)
				return JsonResponse({"msg": "signIn successfully"})
			else:
				return JsonResponse({"msg": "userErr_notActive"})
		else:
			return JsonResponse({"msg": "userErr_notExist"})


class ChangeMsg(View):
	"""修改个人信息视图"""
	def post(self, request):
		return JsonResponse({"changMsg": 1})
