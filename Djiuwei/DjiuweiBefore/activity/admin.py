from django.contrib import admin
from activity.models import Activity, Dynamic, UserActivityMap

# Register your models here.


class ActivityAdmin(admin.ModelAdmin):
	"""活动后台管理"""
	list_display = ("id", "activity_name", "activity_time", "activity_desc", "activity_site", "limit_num", "owner_id", "activity_type")
	list_filter = ("activity_name", "activity_time", "owner_id", "activity_type")
	search_field = ("owner_id", "activity_name", "activity_time", "activity_type")
	ordering = ["activity_type", "activity_time"]

admin.site.register(Activity, ActivityAdmin)


class DynamicAdmin(admin.ModelAdmin):
	"""动态后台管理"""
	list_display = ("id", "comment", "activity_id")
	search_field = ("activity_id",)
	ordering = ["-create_time"]

admin.site.register(Dynamic, DynamicAdmin)


class UserActivityMapAdmin(admin.ModelAdmin):
	"""用户活动关系后台管理"""
	list_display = ("id", "user_id", "activity_id")
	list_filter = ("user_id",)
	search_fields = ("user_id",)
	ordering = ["create_time"]

admin.site.register(UserActivityMap, UserActivityMapAdmin)
