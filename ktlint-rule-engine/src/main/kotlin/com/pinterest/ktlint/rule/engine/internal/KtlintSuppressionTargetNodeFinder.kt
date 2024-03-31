package com.pinterest.ktlint.rule.engine.internal

import com.pinterest.ktlint.rule.engine.api.KtlintSuppression
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.rule.engine.core.api.parent
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

private class DefaultSuppressionTargetNodeFinder : SuppressionTargetNodeFinder {
    override fun findSuppressionTargetNode(astNode: ASTNode): ASTNode = astNode
}

private val defaultSuppressionTargetNodeFinder = DefaultSuppressionTargetNodeFinder()

private class FunctionSuppressionTargetNodeFinder : SuppressionTargetNodeFinder {
    override fun findSuppressionTargetNode(astNode: ASTNode) =
        if (astNode.elementType == ElementType.FUN) {
            astNode
        } else {
            astNode.parent { it.elementType == ElementType.FUN }
        }
}

private val functionSuppressionTargetNodeFinder = FunctionSuppressionTargetNodeFinder()

private class ClassSuppressionTargetNodeFinder : SuppressionTargetNodeFinder {
    override fun findSuppressionTargetNode(astNode: ASTNode) =
        if (astNode.elementType == ElementType.CLASS) {
            astNode
        } else {
            astNode.parent { it.elementType == ElementType.CLASS }
        }
}

private val classSuppressionTargetNodeFinder = ClassSuppressionTargetNodeFinder()

// TODO: Decide in Ktlint 2.x whether it is worth to move the SuppressionTargetNodeFinder into the Rule class. The KtlintRuleEngine should
//  not have any knowledge about how to suppress specific rules.
private val ruleSuppressionTargetNodeFinder: Map<RuleId, SuppressionTargetNodeFinder> =
    mapOf(
        RuleId("standard:class-signature") to classSuppressionTargetNodeFinder,
        RuleId("standard:function-signature") to functionSuppressionTargetNodeFinder,
        RuleId("standard:parameter-list-wrapping") to functionSuppressionTargetNodeFinder,
    )

internal fun KtlintSuppression.findSuppressionTargetNodeFinder() =
    ruleSuppressionTargetNodeFinder[ruleId]
        ?: defaultSuppressionTargetNodeFinder

internal interface SuppressionTargetNodeFinder {
    fun findSuppressionTargetNode(astNode: ASTNode): ASTNode?
}
