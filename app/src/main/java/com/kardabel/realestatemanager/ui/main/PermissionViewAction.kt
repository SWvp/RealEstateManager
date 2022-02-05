package com.kardabel.realestatemanager.ui.main

sealed class PermissionViewAction {

    data class AskPermission(
        val permission : String
    ): PermissionViewAction()
    // SINGLE LIVE EVENT MESSAGE FOR PERMISSION

    PERMISSION_ASKED,
    PERMISSION_DENIED
}