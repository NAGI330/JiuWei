from django.contrib import admin
from user.models import User, RelationShip, UserActivityMap

# Register your models here.

class UserAdmin(admin.ModelAdmin):
	"""用户后台管理"""
	# 后台管理页面展示序列
	list_display = ("id", "username", "email", "is_active", "nickname", "credit_score")
	# 右侧边栏过滤器, 通过指定字段过滤返回结果
	list_filter = ("is_active", "is_superuser")
	# 搜索框, 搜索字段
	search_fields = ("username",)
	# 通过时间层的快速导航栏
	date_hierarchy = "update_time"
	# 按列表字段排序
	ordering = ["create_time", "is_active"]

admin.site.register(User, UserAdmin)


class UserActivityMapAdmin(admin.ModelAdmin):
	"""用户活动关系后台管理"""
	list_display = ("id", "user_id", "activity_id")
	list_filter = ("user_id",)
	search_fields = ("user_id",)
	ordering = ["create_time"]

admin.site.register(UserActivityMap, UserActivityMapAdmin)


class RelationShipAdmin(admin.ModelAdmin):
	"""好友关系后台管理"""
	list_display = ("id", "user_id", "friend")
	list_filter = ("user_id",)
	ordering = ["user_id"]

admin.site.register(RelationShip, RelationShipAdmin)
