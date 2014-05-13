package io.github.seleniumquery.by.evaluator.conditionals.attributes;

import static io.github.seleniumquery.SeleniumQuery.$;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import io.github.seleniumquery.TestInfrastructure;
import io.github.seleniumquery.by.selector.SeleniumQueryCssCompilerIntegrationTest;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class AttributeEvaluatorTest {

	@Before
	public void setUp() {
		$.browser.setDefaultDriver(TestInfrastructure.getDriver());
//		$.browser.setDefaultDriver(new org.openqa.selenium.firefox.FirefoxDriver());
		$.browser.openUrl(TestInfrastructure.getHtmlTestFileUrl(getClass()));
	}
	
	@Test
	public void equalsOrHasAttributeEvaluator() {
		assertAll(EqualsOrHasAttributeEvaluator.EQUALS_ATTRIBUTE_SELECTOR_SYMBOL, "c");
	}
	
	@Test
	public void containsPrefixAttributeEvaluator() {
		// TODO problem here on HtmlUnitDriver, check warnings
		assertAll("|=", "c");
	}
	
	@Test
	public void containsSubstringAttributeEvaluator() {
		assertAll("*=", "c");
	}
	
	@Test
	public void containsWordAttributeEvaluator() {
		// TODO problem here on HtmlUnitDriver, check warnings
		assertAll(ContainsWordAttributeEvaluator.CONTAINS_WORD_ATTRIBUTE_SELECTOR_SYMBOL, "c");
	}
	
	@Test
	public void startsWithAttributeEvaluator() {
		assertAll("^=", "");
	}
	
	@Test
	public void endsWithAttributeEvaluator() {
		assertAll("$=", "c");
	}

	public void assertAll(String attr, String suffix) {
		assertSelectorFindsIds("[title" + attr + "ab" + suffix + "]", "d1", "d2", "d3");
		assertSelectorFindsIds("[title" + attr + "'ab" + suffix + "']", "d1", "d2", "d3");
		assertSelectorFindsIds("[title" + attr + "\"ab" + suffix + "\"]", "d1", "d2", "d3");
		
		if (!($.browser.getDefaultDriver() instanceof HtmlUnitDriver)) {
			// TODO The HtmlUnit driver does not handle this correctly!
			// checked: $= (doesnt work)
			// <div id="d4" title='a"bc'></div>
			assertSelectorFindsIds("[title" + attr + "\"a\\\"b" + suffix + "\"]", "d4");
		}
		
		assertSelectorFindsIds("[title" + attr + "\"a\\'b" + suffix + "\"]", "d5");
		assertSelectorFindsIds("[title" + attr + "\"a'b" + suffix + "\"]", "d5");
		
		assertSelectorFindsIds("[title" + attr + "\"a\\t" + suffix + "\"]");
		assertSelectorFindsIds("[title" + attr + "\"a\\\\t" + suffix + "\"]", "d6");
		
		if (!ContainsWordAttributeEvaluator.CONTAINS_WORD_ATTRIBUTE_SELECTOR_SYMBOL.equals(attr)) {
			assertSelectorFindsIds("[title" + attr + "'a	" + suffix + "']", "d7");
			assertSelectorFindsIds("[title" + attr + "\"a	" + suffix + "\"]", "d7");
			assertSelectorFindsIds("[title" + attr + "\"a\t" + suffix + "\"]", "d7");
		}
		
		assertSelectorFindsIds("[\\31 a2b" + attr + "\"ab" + suffix + "\"]", "d8");
		assertSelectorFindsIds("[-a2b" + attr + "\"ab" + suffix + "\"]", "d9");
		assertSelectorFindsIds("[\\--" + attr + "\"ab" + suffix + "\"]", "d10");
	}
	
	public void assertSelectorFindsIds(String selector, String... ids) {
		List<WebElement> elements = SeleniumQueryCssCompilerIntegrationTest.compileAndExecute(selector);
		assertThat(elements, hasSize(ids.length));
		
		Iterator<WebElement> iterator = elements.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			WebElement webElement = iterator.next();
			String id = ids[i++];
			assertThat(webElement.getAttribute("id"), is(id));
		}
	}

}