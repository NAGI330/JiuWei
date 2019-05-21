from django_redis import get_redis_connection
from user.models import User
from django.shortcuts import get_object_or_404


conn = get_redis_connection("default")


def signIn(user):
	"""保存登录状态"""
	user_hash = user.get_session_auth_hash()
	print("hash: ", user_hash)
	conn.set(user_hash, user.id, 60 * 60 * 2)
	return user_hash


def checkStatus(cookie):
	"""校验登录状态"""
	user_id = int(conn.get(cookie.get("session_id", "")))
	print(user_id)
	return get_object_or_404(User, id=user_id)

def signOut(user):
	"""退出登录"""
	user_hash = user.get_session_auth_hash()
	conn.delete(user_hash)

