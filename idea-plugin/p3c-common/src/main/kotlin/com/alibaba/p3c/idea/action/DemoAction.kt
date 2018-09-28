package com.alibaba.p3c.idea.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class DemoAction: AnAction(){
    override fun actionPerformed(e: AnActionEvent?) {
        Messages.showMessageDialog("helloWord!", "Sample", Messages.getInformationIcon())
    }

}