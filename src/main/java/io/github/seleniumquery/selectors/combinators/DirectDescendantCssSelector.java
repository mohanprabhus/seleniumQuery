package io.github.seleniumquery.selectors.combinators;

import io.github.seleniumquery.selector.CompiledCssSelector;
import io.github.seleniumquery.selector.CssFilter;
import io.github.seleniumquery.selector.CssSelector;
import io.github.seleniumquery.selector.CssSelectorCompilerService;
import io.github.seleniumquery.selector.CssSelectorMatcherService;
import io.github.seleniumquery.selector.SelectorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.w3c.css.sac.DescendantSelector;

public class DirectDescendantCssSelector implements CssSelector<DescendantSelector> {
	
	private static final DirectDescendantCssSelector instance = new DirectDescendantCssSelector();
	public static DirectDescendantCssSelector getInstance() {
		return instance;
	}
	private DirectDescendantCssSelector() { }
	
	/**
	 * PARENT > ELEMENT
	 */
	@Override
	public boolean is(WebDriver driver, WebElement element, Map<String, String> stringMap, DescendantSelector descendantSelector) {
		WebElement parent = SelectorUtils.parent(element);
		if (parent.getTagName().equals("html")) {
			return false;
		}
		return CssSelectorMatcherService.elementMatchesSelector(driver, element, stringMap, descendantSelector.getSimpleSelector())
				&& CssSelectorMatcherService.elementMatchesSelector(driver, parent, stringMap, descendantSelector.getAncestorSelector());
	}
	
	@Override
	public CompiledCssSelector compile(WebDriver driver, Map<String, String> stringMap, DescendantSelector descendantSelector) {
		CompiledCssSelector elementCompiledSelector = CssSelectorCompilerService.compileSelector(driver, stringMap, descendantSelector.getSimpleSelector());
		CompiledCssSelector parentCompiledSelector = CssSelectorCompilerService.compileSelector(driver, stringMap, descendantSelector.getAncestorSelector());
		
		CssFilter childSelectorFilter = new ChildSelectorFilter(parentCompiledSelector, elementCompiledSelector);
		return new CompiledCssSelector(parentCompiledSelector.getCssSelector()+">"+elementCompiledSelector.getCssSelector(),
										childSelectorFilter);
	}
	
	private static final class ChildSelectorFilter implements CssFilter {
		private final CompiledCssSelector parentCompiledSelector;
		private final CompiledCssSelector elementCompiledSelector;
		
		private ChildSelectorFilter(CompiledCssSelector parentCompiledSelector, CompiledCssSelector elementCompiledSelector) {
			this.parentCompiledSelector = parentCompiledSelector;
			this.elementCompiledSelector = elementCompiledSelector;
		}
		
		@Override
		public List<WebElement> filter(WebDriver driver, List<WebElement> elements) {
			elements = elementCompiledSelector.filter(driver, elements);
			
			for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
				WebElement element = iterator.next();
				
				WebElement parent = SelectorUtils.parent(element);
					
				List<WebElement> pf = parentCompiledSelector.filter(driver, new ArrayList<WebElement>(Arrays.asList(parent)));
				boolean parentMatchesTheFilter = !pf.isEmpty();
				if (!parentMatchesTheFilter) {
					// this element's parent is NOT ok, dont keep it
					iterator.remove();
				}
			}
			return elements;
		}
	}

}