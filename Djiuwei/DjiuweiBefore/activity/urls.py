from django.urls import path
from activity.views import CreateActivity, MineActivity, HistoryActivity, TojoinActivity, ChangeActivity, QuitActivity, searchView, joinInActivity, PushHot

app_name = "activity"
urlpatterns = [
	path('createActivity', CreateActivity.as_view(), name="createActivity"),
	path('mineActivity', MineActivity.as_view(), name="mineActivity"),
	path('historyActivity', HistoryActivity.as_view(), name="historyActivity"),
	path('toJoinActivity', TojoinActivity.as_view(), name="tojoinActivity"),
	path('changeActivity', ChangeActivity.as_view(), name="changeActivity"),
	path('quitActivity', QuitActivity.as_view(), name="quitActivity"),
	path('search/<str:q>', searchView.as_view(), name="search"),
	path('joinInActivity', joinInActivity.as_view(), name="join"),
	path('pushActivity', PushHot.as_view(), name="push"),
]

