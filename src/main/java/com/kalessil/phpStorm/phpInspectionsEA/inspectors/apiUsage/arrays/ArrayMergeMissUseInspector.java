package com.kalessil.phpStorm.phpInspectionsEA.inspectors.apiUsage.arrays;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.inspections.PhpInspection;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.kalessil.phpStorm.phpInspectionsEA.fixers.UseSuggestedReplacementFixer;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.FeaturedPhpElementVisitor;
import com.kalessil.phpStorm.phpInspectionsEA.settings.StrictnessCategory;
import com.kalessil.phpStorm.phpInspectionsEA.utils.ExpressionSemanticUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.MessagesPresentationUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiEquivalenceUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/*
 * This file is part of the Php Inspections (EA Extended) package.
 *
 * (c) Vladimir Reznichenko <kalessil@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

public class ArrayMergeMissUseInspector extends PhpInspection {
    private static final String messageUseArray     = "'[...]' would fit more here (it also much faster).";
    private static final String messageArrayUnshift = "'array_unshift(%s, ...)' would fit more here (it also faster).";
    private static final String messageArrayPush    = "'array_push(%s, ...)' would fit more here (it also faster).";
    private static final String messageArraySetItem = "'%s[...] = ...' would fit more here (it also faster).";
    private static final String messageNestedMerge  = "Inlining nested 'array_merge(...)' in arguments is possible here (it also faster).";

    @NotNull
    @Override
    public String getShortName() {
        return "ArrayMergeMissUseInspection";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "'array_merge(...)' misused";
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new FeaturedPhpElementVisitor() {
            @Override
            public void visitPhpFunctionCall(@NotNull FunctionReference reference) {
                if (this.shouldSkipAnalysis(reference, StrictnessCategory.STRICTNESS_CATEGORY_PERFORMANCE)) { return; }

                final String functionName = reference.getName();
                if (functionName != null && functionName.equals("array_merge")) {
                    final PsiElement[] arguments = reference.getParameters();
                    if (arguments.length > 0) {
                        /* case 1: `array_merge([], )` - all arguments are arrays  */
                        if (Arrays.stream(arguments).allMatch(a -> a instanceof ArrayCreationExpression)) {
                            /* false-positive: an array unpacking among arguments */
                            final boolean hasArrayUnpacking = Arrays.stream(arguments).anyMatch(a -> {
                                PsiElement previous = a.getPrevSibling();
                                if (previous instanceof PsiWhiteSpace) {
                                    previous = previous.getPrevSibling();
                                }
                                return OpenapiTypesUtil.is(previous, PhpTokenTypes.opVARIADIC);
                            });
                            if (! hasArrayUnpacking) {
                                final List<String> fragments = new ArrayList<>();
                                Arrays.stream(arguments).forEach(a -> Stream.of(a.getChildren()).forEach(c -> fragments.add(c.getText())));
                                holder.registerProblem(
                                        reference,
                                        MessagesPresentationUtil.prefixWithEa(messageUseArray),
                                        new UseArrayFixer(String.format("[%s]", String.join(", ", fragments)))
                                );
                                fragments.clear();
                            }
                        }

                        /* case 2: `... = array_merge(..., [])`, `... = array_merge([], ...)` - pushing items into array */
                        if (arguments.length == 2 && (arguments[0] instanceof ArrayCreationExpression || arguments[1] instanceof ArrayCreationExpression)) {
                            final PsiElement array       = arguments[0] instanceof ArrayCreationExpression ? arguments[0] : arguments[1];
                            final PsiElement destination = arguments[0] instanceof ArrayCreationExpression ? arguments[1] : arguments[0];
                            final PsiElement[] elements  = array.getChildren();
                            if (elements.length > 0) {
                                final PsiElement parent = reference.getParent();
                                if (OpenapiTypesUtil.isAssignment(parent)) {
                                    final PsiElement container = ((AssignmentExpression) parent).getVariable();
                                    if (container != null && OpenapiEquivalenceUtil.areEqual(container, destination)) {
                                        if (Arrays.stream(elements).anyMatch(e -> ! (e instanceof ArrayHashElement))) {
                                            final List<String> fragments = new ArrayList<>();
                                            if (arguments[0] instanceof ArrayCreationExpression) {
                                                if (destination instanceof Variable) {
                                                    final boolean hasByReference = Arrays.stream(elements)
                                                            .filter(e -> e instanceof PhpPsiElement)
                                                            .map(e -> ((PhpPsiElement) e).getFirstPsiChild())
                                                            .anyMatch(ExpressionSemanticUtil::isByReference);
                                                    if (! hasByReference) {
                                                        fragments.add(destination.getText());
                                                        Arrays.stream(elements).forEach(e -> fragments.add(e.getText()));
                                                        holder.registerProblem(
                                                                parent,
                                                                MessagesPresentationUtil.prefixWithEa(String.format(messageArrayUnshift, destination.getText())),
                                                                new UseArrayUnshiftFixer(String.format("array_unshift(%s)", String.join(", ", fragments)))
                                                        );
                                                    }
                                                }
                                            } else {
                                                fragments.add(destination.getText());
                                                Arrays.stream(elements).forEach(e -> fragments.add(e.getText()));
                                                holder.registerProblem(
                                                        parent,
                                                        MessagesPresentationUtil.prefixWithEa(String.format(messageArrayPush, destination.getText())),
                                                        new UseArrayPushFixer(String.format("array_push(%s)", String.join(", ", fragments)))
                                                );
                                            }
                                            fragments.clear();
                                        } else {
                                            if (elements.length == 1 && destination == arguments[0]) {
                                                final ArrayHashElement hash = (ArrayHashElement) elements[0];
                                                final PsiElement key        = hash.getKey();
                                                final PsiElement value      = hash.getValue();
                                                if (key != null && value != null) {
                                                    holder.registerProblem(
                                                            parent,
                                                            MessagesPresentationUtil.prefixWithEa(String.format(messageArraySetItem, destination.getText())),
                                                            new UseArrayElementAssignmentFixer(String.format("%s[%s] = %s", destination.getText(), key.getText(), value.getText()))
                                                    );
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        /* case 3: `array_merge(..., array_merge(), ...)` - nested calls can be inlined */
                        for (final PsiElement argument : arguments) {
                            if (OpenapiTypesUtil.isFunctionReference(argument)) {
                                final String innerFunctionName = ((FunctionReference) argument).getName();
                                if (innerFunctionName != null && innerFunctionName.equals("array_merge")) {
                                    final List<String> fragments = new ArrayList<>();
                                    for (final PsiElement fragment : arguments) {
                                        if (OpenapiTypesUtil.isFunctionReference(fragment)) {
                                            final FunctionReference innerCall = (FunctionReference) fragment;
                                            if (innerFunctionName.equals(innerCall.getName())) {
                                                Arrays.stream(innerCall.getParameters()).forEach(p -> fragments.add(p.getText()));
                                                continue;
                                            }
                                        }
                                        fragments.add(fragment.getText());
                                    }
                                    holder.registerProblem(
                                            reference,
                                            MessagesPresentationUtil.prefixWithEa(messageNestedMerge),
                                            new InlineNestedCallsFixer(String.format("array_merge(%s)", String.join(", ", fragments)))
                                    );
                                    fragments.clear();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static final class UseArrayPushFixer extends UseSuggestedReplacementFixer {
        private static final String title = "Use array_push(...) instead";

        @NotNull
        @Override
        public String getName() {
            return MessagesPresentationUtil.prefixWithEa(title);
        }

        UseArrayPushFixer(@NotNull String expression) {
            super(expression);
        }
    }

    private static final class UseArrayUnshiftFixer extends UseSuggestedReplacementFixer {
        private static final String title = "Use array_unshift(...) instead";

        @NotNull
        @Override
        public String getName() {
            return MessagesPresentationUtil.prefixWithEa(title);
        }

        UseArrayUnshiftFixer(@NotNull String expression) {
            super(expression);
        }
    }

    private static final class InlineNestedCallsFixer extends UseSuggestedReplacementFixer {
        private static final String title = "Inline nested array_merge(...) calls";

        @NotNull
        @Override
        public String getName() {
            return MessagesPresentationUtil.prefixWithEa(title);
        }

        InlineNestedCallsFixer(@NotNull String expression) {
            super(expression);
        }
    }

    private static final class UseArrayFixer extends UseSuggestedReplacementFixer {
        private static final String title = "Replace with array declaration";

        @NotNull
        @Override
        public String getName() {
            return MessagesPresentationUtil.prefixWithEa(title);
        }

        UseArrayFixer(@NotNull String expression) {
            super(expression);
        }
    }

    private static final class UseArrayElementAssignmentFixer extends UseSuggestedReplacementFixer {
        private static final String title = "Replace with array element assignment";

        @NotNull
        @Override
        public String getName() {
            return MessagesPresentationUtil.prefixWithEa(title);
        }

        UseArrayElementAssignmentFixer(@NotNull String expression) {
            super(expression);
        }
    }
}
