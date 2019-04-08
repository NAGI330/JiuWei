from django.urls import path
from user.views import ActiveView, SignInView, SignOnView

app_name = "user"
urlpatterns = [
	path('Active/<str:token>', ActiveView.as_view(), name="active"),
	path('SignOn', SignOnView.as_view(), name="signOn"),
	path('SignIn', SignInView.as_view(), name="signIN"),
]
