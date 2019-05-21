from django.urls import path
from user.views import ActiveView, SignInView, SignOnView, changeMsg, changePassword, getBackPwd, getBackPassword

app_name = "user"
urlpatterns = [
	path('Active/<str:token>', ActiveView.as_view(), name="active"),
	path('SignOn', SignOnView.as_view(), name="signOn"),
	path('SignIn', SignInView.as_view(), name="signIN"),
	path('changeUserMsg', changeMsg.as_view(), name="changeUserMsg"),
	path('changePwd', changePassword.as_view(), name="changePwd"),
	path('RetrievePwd', getBackPassword.as_view(), name="RetrievePwd"),
	path('confirm/<str:token>', getBackPwd.as_view(), name="confirm"),
]
