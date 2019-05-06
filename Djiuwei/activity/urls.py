from django.urls import path
from activity.views import CreateActivity, MineActivity, HistoryActivity, TojoinActivity, ChangeActivity, QuitActivity

app_name = "activity"
urlpatterns = [
	path('createActivity', CreateActivity.as_view(), name="createActivity"),
	path('mineActivity', MineActivity.as_view(), name="mineActivity"),
	path('historyActivity', HistoryActivity.as_view(), name="historyActivity"),
	path('toJoinActivity', ToJoinActivity.as_view(), name="toJoinActivity"),
	path('changeActivity', ChangeActivity.as_view(), name="changeActivity"),
	path('quitActivity', QuitActivity.as_view(), name="quitActivity"),
]

