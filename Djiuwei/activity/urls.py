from django.urls import path
from activity.views import CreateActivity, MyActivity, HistoryActivity, TojoinActivity, ChangeActivity, QuitActivity

app_name = "activity"
urlpatterns = [
	path('createActivity', CreateActivity.as_view(), name="createActivity"),
	path('myActivity', MyActivity.as_view(), name="myActivity"),
	path('historyActivity', HistoryActivity.as_view(), name="historyActivity"),
	path('tojoinActivity', TojoinActivity.as_view(), name="tojoinActivity"),
	path('changeActivity', ChangeActivity.as_view(), name="changeActivity"),
	path('quitActivity', QuitActivity.as_view(), name="quitActivity"),
]

