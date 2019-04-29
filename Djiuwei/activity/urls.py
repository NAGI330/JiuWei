from django.urls import path
from activity.views import CreateActivity
from activity.views import MyActivity
from activity.views import HistoryActivity
from activity.views import ToJoinActivity

app_name = "activity"
urlpatterns = [
	path('createActivity', CreateActivity.as_view(), name="createActivity"),
	path('myActivity', MyActivity.as_view(), name="myActivity"),
	path('historyActivity', HistoryActivity.as_view(), name="historyActivity"),
	path('tojoinActivity', ToJoinActivity.as_view(), name="tojoinActivity"),

]

