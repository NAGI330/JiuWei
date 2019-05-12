from haystack import indexes
from datetime import datetime
from .models import Activity


class PostIndex(indexes.SearchIndex, indexes.Indexable):
	"""post索引, 告知haystack那些数据需要被编入搜索索引"""
	# 主要搜索字段
	text = indexes.CharField(document=True, use_template=True)

	def get_model(self):
		return Activity

	def index_queryset(self, using=None):
		now = datetime.now()
		return self.get_model().objects.filter(activity_time__gt=now)

