package jmri.jmrit.logixng.expressions.swing;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.swing.JPanel;

import jmri.*;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.implementation.DefaultSymbolTable;
import jmri.jmrit.logixng.swing.AbstractSwingConfigurator;
import jmri.util.swing.JmriJOptionPane;

/**
 * Abstract class for SwingConfiguratorInterface
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public abstract class AbstractDigitalExpressionSwing extends AbstractSwingConfigurator {

    protected JPanel panel;
    
    /** {@inheritDoc} */
    @Override
    public String getExecuteEvaluateMenuText() {
        return Bundle.getMessage("MenuText_ExecuteEvaluate");
    }
    
    /** {@inheritDoc} */
    @Override
    public void executeEvaluate(@Nonnull Base object) {
        ConditionalNG conditionalNG = object.getConditionalNG();
        if (conditionalNG == null) throw new RuntimeException("Not supported yet");
        
        SymbolTable symbolTable = new DefaultSymbolTable();
        getAllSymbols(object, symbolTable);
        
        conditionalNG.getCurrentThread().runOnLogixNGEventually(() -> {
            SymbolTable oldSymbolTable = conditionalNG.getSymbolTable();
            
            try {
                conditionalNG.setSymbolTable(symbolTable);
                boolean result = ((DigitalExpression)object).evaluate();
                jmri.util.ThreadingUtil.runOnGUIEventually(() -> {
                    JmriJOptionPane.showMessageDialog(null,
                            Bundle.getMessage("ExecuteEvaluate_EvaluationCompleted", result),
                            Bundle.getMessage("ExecuteEvaluate_Title"),
                            JmriJOptionPane.PLAIN_MESSAGE);
                });
            } catch (JmriException | RuntimeException e) {
                log.warn("ConditionalNG {} got an exception during execute: {}",
                        conditionalNG.getSystemName(), e, e);
            }
            
            conditionalNG.setSymbolTable(oldSymbolTable);
        });
    }
    
    /** {@inheritDoc} */
    @Override
    public BaseManager<? extends NamedBean> getManager() {
        return InstanceManager.getDefault(DigitalExpressionManager.class);
    }
    
    /** {@inheritDoc} */
    @Override
    public JPanel getConfigPanel(@Nonnull JPanel buttonPanel) throws IllegalArgumentException {
        createPanel(null, buttonPanel);
        return panel;
    }
    
    /** {@inheritDoc} */
    @Override
    public JPanel getConfigPanel(@Nonnull Base object, @Nonnull JPanel buttonPanel) throws IllegalArgumentException {
        createPanel(object, buttonPanel);
        return panel;
    }
    
    protected abstract void createPanel(@CheckForNull Base object, @Nonnull JPanel buttonPanel);
    
    /** {@inheritDoc} */
    @Override
    public String getExampleSystemName() {
        return InstanceManager.getDefault(DigitalExpressionManager.class).getSystemNamePrefix() + "DE10";
    }
    
    /** {@inheritDoc} */
    @Override
    public String getAutoSystemName() {
        return InstanceManager.getDefault(DigitalExpressionManager.class).getAutoSystemName();
    }
    
    
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AbstractDigitalExpressionSwing.class);
    
}
