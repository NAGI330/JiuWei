# Generated by Django 2.1.7 on 2019-04-08 08:37

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('user', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='relationship',
            old_name='friend_id',
            new_name='friend',
        ),
        migrations.RenameField(
            model_name='relationship',
            old_name='user_id',
            new_name='user',
        ),
        migrations.RenameField(
            model_name='useractivitymap',
            old_name='activity_id',
            new_name='activity',
        ),
        migrations.RenameField(
            model_name='useractivitymap',
            old_name='user_id',
            new_name='user',
        ),
    ]
