# Generated by Django 2.1.7 on 2019-04-15 12:17

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('activity', '0004_auto_20190415_2010'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='activity',
            name='limit_requirement',
        ),
    ]