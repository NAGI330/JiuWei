from django.shortcuts import get_object_or_404
from django.views.generic import View
import json
from django.http import JsonResponse
from user.models import User
from django.core.mail import send_mail
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from itsdangerous import SignatureExpired
from django.conf import settings
from django.contrib.auth import authenticate
from celery_tasks.tasks import send_register_active_email
from utils.user_status import signIn, checkStatus, signOut

# Create your views here.

class SignOnView(View):
	"""注册视图"""
	def post(self, request):
		"""注册校验"""
		# 1.请求数据校验
		try:
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
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

		# 加密
		serializer = Serializer(settings.SECRET_KEY, 120)
		info = {"username": username, "password": password, "email": email}
		token = serializer.dumps(info)
		token = token.decode("utf-8")
		
		# 邮件信息
		send_register_active_email.delay(email, username, token)
		return JsonResponse({"msg": "signOn successfully"})


class ActiveView(View):
	"""邮箱激活"""
	def get(self, request, token):
		"""进行用户激活"""
		# 解密
		serializer = Serializer(settings.SECRET_KEY, 120)
		try:
			info = serializer.loads(token)
			username = info['username']
			password = info['password']
			email = info['email']
			user = User.objects.create_user(username, email, password)
			user.nickname = "用户" + user.get_session_auth_hash()[:6]
			user.is_active = 1
			user.save()
			return JsonResponse({"msg": "user is active"})
		except SignatureExpired as e:
			return JsonResponse({"msg": "active is out of time"})


def get_info(user):
	"""个人信息"""
	info = {"id": user.id, "nickname": user.nickname, "email": user.email, "gender": user.gender, "age": user.age, "credit_score": user.credit_score, "self_desc": user.self_desc}
	return info


class SignInView(View):
	"""登录视图"""
	def post(self, request):
		"""登录校验"""
		# 1.请求数据校验
		try:
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
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
				session_id = signIn(user)
				cookie = {"session_id": session_id}
				info = get_info(user)
				return JsonResponse({"msg": "signIn successfully", "Cookie": cookie, "info": info})
			else:
				return JsonResponse({"msg": "userErr_notActive"})
		else:
			return JsonResponse({"msg": "userErr_notExist"})


class changeMsg(View):
	"""修改个人信息视图"""
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

		# 未激活用户不能修改
		if not user.is_active:
			return JsonResponse({"msg": "userErr_unActive"})

		# 修改信息
		user.nickname = request_msg.get("nickname", "")
		user.gender = int(request_msg.get("gender", 1))
		user.age = int(request_msg.get("age", 18))
		user.head_img = request_msg.get("head_img", "")
		user.self_desc = request_msg.get("self_desc", "")

		# 修改入库
		user.save()

		return JsonResponse({"msg": "change successfully"})


class changePassword(View):
	"""修改密码"""
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

		# 未激活用户不能修改
		if not user.is_active:
			return JsonResponse({"msg": "userErr_unActive"})

		# 修改信息
		password = request_msg.get("password", "")
		user.set_password(password)
		user.save()

		return JsonResponse({"msg": "change successfully"})


class getBackPassword(View):
	"""修改密码"""
	def post(self, request):
		try:
			# 请求格式错误处理
			request_msg = json.loads(request.body)
			if not isinstance(request_msg, dict):
				return JsonResponse({"msg": "typeErr_dict"})
		except Exception as e:
			request_msg = {}

		# 获取数据
		username = request_msg.get("username", "")
		password = request_msg.get("password", "")
		try:
			user = User.objects.get(username=username)
		except Exception as e:
			return JsonResponse({"msg": "userErr_notExist"})

		email = user.email
		# 加密
		serializer = Serializer(settings.SECRET_KEY, 120)
		info = {"username": username, "password": password}
		token = serializer.dumps(info)
		token = token.decode("utf-8")
		
		# 邮件信息
		send_register_active_email.delay(email, username, token)

		return JsonResponse({"msg": "retrieve successfully"})


class getBackPwd(View):
	"""确认修改密码"""
	def get(self, request, token):
		"""进行密码更改确认"""
		# 解密
		serializer = Serializer(settings.SECRET_KEY, 120)
		try:
			info = serializer.loads(token)
			username = info['username']
			password = info['password']
			user = User.objects.get(username=username)
			user.set_password(password)
			user.save()
			return JsonResponse({"msg": "pwd is changed"})
		except SignatureExpired as e:
			return JsonResponse({"msg": "get back pwd is out of time"})


