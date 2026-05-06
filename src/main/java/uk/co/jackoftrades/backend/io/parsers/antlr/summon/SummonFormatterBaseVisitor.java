// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.summon;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link SummonFormatterVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
@SuppressWarnings("CheckReturnValue")
public class SummonFormatterBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements SummonFormatterVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitName(SummonFormatterParser.NameContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitMsgt(SummonFormatterParser.MsgtContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitUniques(SummonFormatterParser.UniquesContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitBase(SummonFormatterParser.BaseContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitRaceFlag(SummonFormatterParser.RaceFlagContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitFallback(SummonFormatterParser.FallbackContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitDesc(SummonFormatterParser.DescContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitSummon(SummonFormatterParser.SummonContext ctx) {
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override
	public T visitFile(SummonFormatterParser.FileContext ctx) {
		return visitChildren(ctx);
	}
}