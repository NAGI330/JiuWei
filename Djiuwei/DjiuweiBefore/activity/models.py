from django.db import models
from db.base_model import BaseModel
from user.models import User

# Create your models here.


class Activity(BaseModel):
	"""活动模型类"""
	activity_name = models.CharField(max_length=20, verbose_name="活动名称")
	activity_desc = models.CharField(max_length=100, verbose_name="活动描述")
	activity_time = models.DateTimeField(verbose_name="活动时间")
	activity_site = models.CharField(max_length=50, verbose_name="活动地点")
	limit_num = models.IntegerField(default=10, verbose_name="人数限制")
	owner = models.ForeignKey(User, verbose_name="发起人id", on_delete=models.CASCADE)
	# limit_requirement = models.CharField(max_length=100, verbose_name="限制条件")
	activity_type = models.CharField(max_length=20, verbose_name="活动类型")

	class Meta:
		db_table = "jw_activity"
		verbose_name = "活动"
		verbose_name_plural = verbose_name

	def __str__(self):
		return self.activity_name


class UserActivityMap(BaseModel):
	"""用户和活动关系映射"""
	user = models.ForeignKey(User, verbose_name="用户id", on_delete=models.CASCADE)
	activity = models.ForeignKey(Activity, verbose_name="活动id", on_delete=models.CASCADE)

	class Meta:
		db_table = "jw_uamap"
		verbose_name = "用户活动映射"
		verbose_name_plural = verbose_name

	def __str__(self):
		return "{}".format(self.user_id)


class Dynamic(BaseModel):
	"""动态表"""
	activity = models.ForeignKey(Activity, verbose_name="活动id", on_delete=models.CASCADE)
	comment = models.CharField(max_length=150, null=True, verbose_name="评论")
	img = models.ImageField(upload_to='images', null=True, verbose_name="图片")

	class Meta:
		db_table = "jw_dynamic"
		verbose_name = "动态"
		verbose_name_plural = verbose_name

	def __str__(self):
		return "{}".format(self.activity_id)

