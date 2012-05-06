package name.abuchen.portfolio.ui.dialogs;

import name.abuchen.portfolio.ui.util.BindingHelper;
import name.abuchen.portfolio.ui.util.BindingHelper.Model;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/* package */abstract class AbstractDialog extends Dialog
{
    private String title;
    private Model model;

    protected BindingHelper bindings;

    public AbstractDialog(Shell parentShell, String title, Model model)
    {
        super(parentShell);
        this.title = title;
        this.model = model;
        this.bindings = new BindingHelper(model)
        {
            @Override
            public void onValidationStatusChanged(IStatus status)
            {
                Control button = getButton(IDialogConstants.OK_ID);
                if (button != null)
                    button.setEnabled(status.getSeverity() == IStatus.OK);
            }
        };
    }

    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(title);
    }

    protected Model getModel()
    {
        return model;
    }

    protected DataBindingContext getBindingContext()
    {
        return bindings.getBindingContext();
    }

    @Override
    protected final Control createContents(Composite parent)
    {
        Control answer = super.createContents(parent);

        bindings.onValidationStatusChanged(AggregateValidationStatus.getStatusMaxSeverity(getBindingContext()
                        .getValidationStatusProviders()));

        return answer;
    }

    protected final Control createDialogArea(Composite parent)
    {
        Composite composite = (Composite) super.createDialogArea(parent);

        Composite editArea = new Composite(composite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(editArea);
        editArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        createFormElements(editArea);

        bindings.createErrorLabel(editArea);

        return composite;
    }

    protected abstract void createFormElements(Composite editArea);

    @Override
    protected void okPressed()
    {
        model.applyChanges();
        super.okPressed();
    }

}