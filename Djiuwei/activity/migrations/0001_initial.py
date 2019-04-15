# Generated by Django 2.1.7 on 2019-04-08 08:22

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Activity',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('create_time', models.DateTimeField(auto_now_add=True, verbose_name='创建时间')),
                ('update_time', models.DateTimeField(auto_now=True, verbose_name='更新时间')),
                ('is_delete', models.BooleanField(default=False, verbose_name='删除标记')),
                ('activity_name', models.CharField(max_length=20, verbose_name='活动名称')),
                ('activity_desc', models.CharField(max_length=100, verbose_name='活动描述')),
                ('activity_time', models.DateTimeField(verbose_name='活动时间')),
                ('activity_site', models.CharField(max_length=50, verbose_name='活动地点')),
                ('limit_num', models.IntegerField(default=10, verbose_name='人数限制')),
                ('owner_id', models.IntegerField(verbose_name='发起人id')),
                ('limit_requirement', models.CharField(max_length=100, verbose_name='限制条件')),
                ('activity_type', models.CharField(max_length=20, verbose_name='活动类型')),
            ],
            options={
                'verbose_name': '活动',
                'verbose_name_plural': '活动',
                'db_table': 'jw_activity',
            },
        ),
        migrations.CreateModel(
            name='Dynamic',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('create_time', models.DateTimeField(auto_now_add=True, verbose_name='创建时间')),
                ('update_time', models.DateTimeField(auto_now=True, verbose_name='更新时间')),
                ('is_delete', models.BooleanField(default=False, verbose_name='删除标记')),
                ('comment', models.CharField(max_length=150, verbose_name='评论')),
                ('img', models.ImageField(upload_to='images', verbose_name='图片')),
                ('activity', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='activity.Activity', verbose_name='活动id')),
            ],
            options={
                'verbose_name': '动态',
                'verbose_name_plural': '动态',
                'db_table': 'jw_dynamic',
            },
        ),
    ]