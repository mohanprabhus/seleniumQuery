package io.github.seleniumquery.by.evaluator.conditionals.attributes;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import io.github.seleniumquery.by.evaluator.CSSCondition;
import io.github.seleniumquery.by.selector.CompiledSelector;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.Selector;

public class StartsWithAttributeEvaluator implements CSSCondition<AttributeCondition> {

	private static final StartsWithAttributeEvaluator instance = new StartsWithAttributeEvaluator();
	public static StartsWithAttributeEvaluator getInstance() {
		return instance;
	}
	private StartsWithAttributeEvaluator() { }
	
	
	public static final String STARTS_WITH_ATTRIBUTE_SELECTOR_SYMBOL = "^=";

	/**
	 * Currently it is (mistakenly?) mapped to the type {@link org.w3c.css.sac.Condition#SAC_ATTRIBUTE_CONDITION}.
	 * The factory then inspects the actual type and redirects here.
	 * 
	 * This selector is:
	 * [attribute^=stringToStart]
	 * 
	 * CASE INsensitive!
	 */
	@Override
	public boolean is(WebDriver driver, WebElement element, Selector selectorUpToThisPoint, AttributeCondition attributeCondition) {
		String wantedValue = attributeCondition.getValue();
		String actualValue = element.getAttribute(attributeCondition.getLocalName());
		return startsWithIgnoreCase(actualValue, wantedValue);
	}

	@Override
	public CompiledSelector compile(WebDriver driver, Selector simpleSelector, AttributeCondition attributeCondition) {
		// nothing to do, everyone supports this selector
		return AttributeEvaluatorUtils.createAttributeNoFilterCompiledSelector(attributeCondition, STARTS_WITH_ATTRIBUTE_SELECTOR_SYMBOL);
	}

}