from django.urls import path
from user.views import ActiveView

app_name = "user"
urlpatterns = [
	path('', ActiveView.as_view(), name="active"),
]
