from django.db import models
from django.contrib.auth.models import AbstractUser
from db.base_model import BaseModel

# Create your models here.


class User(AbstractUser, BaseModel):
	"""用户模型类"""
	nickname = models.CharField(max_length=20, null=True, verbose_name="昵称")
	gender = models.BooleanField(default=1, null=True, verbose_name="性别")
	age = models.IntegerField(default=18, null=True, verbose_name="年龄")
	head_img = models.ImageField(upload_to='images', null=True, verbose_name="头像")
	credit_score = models.IntegerField(default=80, verbose_name="信用积分")
	self_desc = models.CharField(max_length=100, null=True, verbose_name="个人描述")

	class Meta:
		db_table = "jw_user"
		verbose_name = "用户"
		verbose_name_plural = verbose_name		

	def __str__(self):
		return self.username


class RelationShip(BaseModel):
	"""好友关系表"""
	user = models.ForeignKey(User, verbose_name="用户id", on_delete=models.CASCADE)
	friend = models.IntegerField(verbose_name="好友id")
	
	class Meta:
		db_table = "jw_relationship"
		verbose_name = "好友关系"
		verbose_name_plural = verbose_name

	def __str__(self):
		return self.user_id
