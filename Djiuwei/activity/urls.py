from django.urls import path
from activity.views import CreateActivity

app_name = "activity"
urlpatterns = [
	path('createActivity', CreateActivity.as_view(), name="createActivity"),
]

