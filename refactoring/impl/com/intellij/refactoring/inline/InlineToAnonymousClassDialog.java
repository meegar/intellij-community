package com.intellij.refactoring.inline;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiCall;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.RefactoringSettings;

import javax.swing.*;
import java.awt.*;

/**
 * @author yole
 */
public class InlineToAnonymousClassDialog extends InlineOptionsDialog {
  private final PsiClass myClass;
  private final PsiCall myCallToInline;
  private JCheckBox myCbSearchInComments;
  private JCheckBox myCbSearchTextOccurences;

  protected InlineToAnonymousClassDialog(Project project, PsiClass psiClass, final PsiCall callToInline) {
    super(project, true, psiClass);
    myClass = psiClass;
    myCallToInline = callToInline;
    myInvokedOnReference = (myCallToInline != null);
    setTitle(RefactoringBundle.message("inline.to.anonymous.refactoring"));
    init();
  }

  protected String getNameLabelText() {
    String className = PsiFormatUtil.formatClass(myClass, PsiFormatUtil.SHOW_NAME);
    return RefactoringBundle.message("inline.to.anonymous.name.label", className);
  }

  protected String getBorderTitle() {
    return RefactoringBundle.message("inline.to.anonymous.border.title");
  }

  protected String getInlineAllText() {
    return RefactoringBundle.message("all.references.and.remove.the.class");
  }

  protected String getInlineThisText() {
    return RefactoringBundle.message("this.reference.only.and.keep.the.class");
  }

  protected boolean isInlineThis() {
    return false;
  }

  protected JComponent createCenterPanel() {
    JComponent optionsPanel = super.createCenterPanel();

    // TODO[yole]: make visible again when "inline this" option is fixed (IDEADEV-17928)
    myOptionsPanel.setVisible(false);
    myRbInlineAll.setSelected(true);
    
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(optionsPanel, gbc);

    RefactoringSettings settings = RefactoringSettings.getInstance();
    myCbSearchInComments = new JCheckBox(RefactoringBundle.message("search.in.comments.and.strings"),
                                         settings.INLINE_CLASS_SEARCH_IN_COMMENTS);
    myCbSearchTextOccurences = new JCheckBox(RefactoringBundle.message("search.for.text.occurrences"),
                                             settings.INLINE_CLASS_SEARCH_IN_NON_JAVA);
    gbc.gridy = 1;
    panel.add(myCbSearchInComments, gbc);
    gbc.gridy = 2;
    panel.add(myCbSearchTextOccurences, gbc);
    return panel;
  }

  protected void doAction() {
    final boolean searchInComments = myCbSearchInComments.isSelected();
    final boolean searchInNonJava = myCbSearchTextOccurences.isSelected();

    RefactoringSettings settings = RefactoringSettings.getInstance();
    settings.INLINE_CLASS_SEARCH_IN_COMMENTS = searchInComments;
    settings.INLINE_CLASS_SEARCH_IN_NON_JAVA = searchInNonJava;

    invokeRefactoring(new InlineToAnonymousClassProcessor(getProject(), myClass, myCallToInline, isInlineThisOnly(),
                                                          searchInComments, searchInNonJava));
  }
}