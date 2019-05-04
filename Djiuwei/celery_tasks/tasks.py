from celery import Celery
from django.conf import settings
from django.core.mail import send_mail
import os
import django


# 创建一个Celery的实例对象
app = Celery("celery_tasks.tasks", broker="redis://:password@127.0.0.1:6379/1")
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'Djiuwei.settings')
django.setup()

# 定义任务函数
@app.task
def send_register_active_email(to_email, username, token):
	"""发送激活邮件"""
	subject = "久违欢迎你"
	message = ""
	sender = settings.EMAIL_HOST_USER
	receiver = [to_email]
	html_message = "<h1>尊敬的{}, 欢迎您注册成为久违的用户</h1>请点击下面的链接激活您的账户<br /><a href='http://127.0.0.1:8000/personal/Active/{}'>http://127.0.0.1/personal/Active/{}</a>".format(username, token, token)
	try:
		send_mail(subject, message, sender, receiver, html_message=html_message)
		print("send_mail successfully")
	except Exception as e:
		print(e)

