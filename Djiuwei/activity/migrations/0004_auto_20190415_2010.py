# Generated by Django 2.1.7 on 2019-04-15 12:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('activity', '0003_auto_20190415_1947'),
    ]

    operations = [
        migrations.AlterField(
            model_name='dynamic',
            name='comment',
            field=models.CharField(max_length=150, null=True, verbose_name='评论'),
        ),
        migrations.AlterField(
            model_name='dynamic',
            name='img',
            field=models.ImageField(null=True, upload_to='images', verbose_name='图片'),
        ),
    ]
