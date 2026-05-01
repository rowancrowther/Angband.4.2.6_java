// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ConstantsFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link ConstantsFormatterVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
@SuppressWarnings("CheckReturnValue")
public class ConstantsFormatterBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements ConstantsFormatterVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitSection(ConstantsFormatterParser.SectionContext ctx) {
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitFurtherValue(ConstantsFormatterParser.FurtherValueContext ctx) {
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitMultiValue(ConstantsFormatterParser.MultiValueContext ctx) {
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitLine(ConstantsFormatterParser.LineContext ctx) {
		return visitChildren(ctx);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitFile(ConstantsFormatterParser.FileContext ctx) {
		return visitChildren(ctx);
	}
}