package com.kalessil.phpStorm.phpInspectionsEA.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * This file is part of the Php Inspections (EA Extended) package.
 *
 * (c) Vladimir Reznichenko <kalessil@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

final public class OpenapiTypesUtil {
    final public static TokenSet tsCOMPARE_EQUALITY_OPS = TokenSet.create(
            PhpTokenTypes.opEQUAL,
            PhpTokenTypes.opNOT_EQUAL,
            PhpTokenTypes.opIDENTICAL,
            PhpTokenTypes.opNOT_IDENTICAL
            /* PS 2017.3 included instanceof here, hence we are duplicating the TS */
    );

    final public static TokenSet DEFAULT_VALUES = TokenSet.create(
            PhpElementTypes.CONSTANT_REF,
            PhpElementTypes.STRING,
            PhpElementTypes.NUMBER,
            PhpElementTypes.CLASS_CONSTANT_REFERENCE,
            PhpElementTypes.ARRAY_CREATION_EXPRESSION
    );

    static public boolean isLambda(@Nullable PsiElement expression) {
        if (is(expression, PhpElementTypes.CLOSURE)) {
            expression = expression.getFirstChild();
        }
        return expression instanceof Function && ((Function) expression).isClosure();
    }

    static public boolean isString(@Nullable PsiElement expression) {
        return expression != null && expression.getNode().getElementType() == PhpElementTypes.STRING;
    }

    static public boolean isAssignment(@Nullable PsiElement expression) {
        if (is(expression, PhpElementTypes.ASSIGNMENT_EXPRESSION)) {
            return OpenapiPsiSearchUtil.findAssignmentOperator((AssignmentExpression) expression) != null;
        }
        return false;
    }

    static public boolean isAssignmentByReference(@Nullable AssignmentExpression assignment) {
        final PsiElement operator = OpenapiPsiSearchUtil.findAssignmentOperator(assignment);
        if (operator != null) {
            final int characters = operator.getTextLength();
            return characters != 1 && operator.getText().replaceAll("\\s+", "").equals("=&");
        }
        return false;
    }

    static public boolean isByReference(@Nullable PsiElement expression) {
        if (expression != null) {
            final PsiElement candidate = expression.getPrevSibling();
            final PsiElement previous  = candidate instanceof PsiWhiteSpace ? candidate.getPrevSibling() : candidate;
            return OpenapiTypesUtil.is(previous, PhpTokenTypes.opBIT_AND);
        }
        return false;
    }

    static public boolean isFunctionReference(@Nullable PsiElement expression) {
        return expression != null && expression.getNode().getElementType() == PhpElementTypes.FUNCTION_CALL;
    }

    static public boolean isLoop(@Nullable PsiElement expression) {
        return expression instanceof ForeachStatement ||
               expression instanceof For   ||
               expression instanceof While ||
               expression instanceof DoWhile;
    }

    static public boolean isStatementImpl(@Nullable PsiElement expression) {
        return expression != null && expression.getNode().getElementType() == PhpElementTypes.STATEMENT;
    }

    static public boolean isPhpExpressionImpl(@Nullable PsiElement expression) {
        return expression != null && expression.getNode().getElementType() == PhpElementTypes.EXPRESSION;
    }

    static public boolean isNumber(@Nullable PsiElement expression) {
        boolean result = false;
        if (expression != null) {
            /* regular numbers */
            result = expression.getNode().getElementType() == PhpElementTypes.NUMBER;
            /* negative numbers */
            if (!result && expression instanceof UnaryExpression) {
                final UnaryExpression unary = (UnaryExpression) expression;
                result = is(unary.getOperation(), PhpTokenTypes.opMINUS) && is(unary.getValue(), PhpElementTypes.NUMBER);
            }
        }
        return result;
    }

    static public boolean isThrowExpression(@Nullable PsiElement expression) {
        if (expression != null) {
            // PS 2020.3, 2021.1 has changed the throw structure, hence we have to rely on low-level structures.
            final boolean possiblyThrow = expression instanceof StatementWithArgument;
            if (possiblyThrow && OpenapiTypesUtil.is(expression.getFirstChild(), PhpTokenTypes.kwTHROW)) {
                return true;
            }
        }
        return false;
    }

    static public boolean is(@Nullable PsiElement expression, @NotNull IElementType type) {
        return expression != null && expression.getNode().getElementType() == type;
    }
}
