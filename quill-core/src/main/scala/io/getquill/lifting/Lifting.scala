package io.getquill.lifting

import scala.reflect.macros.whitebox.Context

import io.getquill.ast.Add
import io.getquill.ast.And
import io.getquill.ast.Constant
import io.getquill.ast.Division
import io.getquill.ast.Equals
import io.getquill.ast.Expr
import io.getquill.ast.Filter
import io.getquill.ast.FlatMap
import io.getquill.ast.FunctionApply
import io.getquill.ast.FunctionDef
import io.getquill.ast.GreaterThan
import io.getquill.ast.GreaterThanOrEqual
import io.getquill.ast.Ident
import io.getquill.ast.LessThan
import io.getquill.ast.LessThanOrEqual
import io.getquill.ast.Map
import io.getquill.ast.NullValue
import io.getquill.ast.Parametrized
import io.getquill.ast.ParametrizedExpr
import io.getquill.ast.ParametrizedQuery
import io.getquill.ast.Property
import io.getquill.ast.Query
import io.getquill.ast.Ref
import io.getquill.ast.Remainder
import io.getquill.ast.Subtract
import io.getquill.ast.Table
import io.getquill.ast.Tuple
import io.getquill.ast.Value

trait Lifting {
  val c: Context
  import c.universe.{ Function => _, Expr => _, Ident => _, Constant => _, _ }

  implicit val queryLift: Liftable[Query] = Liftable[Query] {
    case Table(name) =>
      q"io.getquill.ast.Table($name)"
    case Filter(query, alias, body) =>
      q"io.getquill.ast.Filter($query, $alias, $body)"
    case Map(query, alias, body) =>
      q"io.getquill.ast.Map($query, $alias, $body)"
    case FlatMap(query, alias, body) =>
      q"io.getquill.ast.FlatMap($query, $alias, $body)"
  }

  implicit val exprLift: Liftable[Expr] = Liftable[Expr] {
    case FunctionApply(ident, value) =>
      q"io.getquill.ast.FunctionApply($ident, $value)"
    case FunctionDef(ident, body) =>
      q"io.getquill.ast.FunctionDef($ident, $body)"
    case Subtract(a, b) =>
      q"io.getquill.ast.Subtract($a, $b)"
    case Division(a, b) =>
      q"io.getquill.ast.Division($a, $b)"
    case Remainder(a, b) =>
      q"io.getquill.ast.Remainder($a, $b)"
    case Add(a, b) =>
      q"io.getquill.ast.Add($a, $b)"
    case Equals(a, b) =>
      q"io.getquill.ast.Equals($a, $b)"
    case And(a, b) =>
      q"io.getquill.ast.And($a, $b)"
    case GreaterThan(a, b) =>
      q"io.getquill.ast.GreaterThan($a, $b)"
    case GreaterThanOrEqual(a, b) =>
      q"io.getquill.ast.GreaterThanOrEqual($a, $b)"
    case LessThan(a, b) =>
      q"io.getquill.ast.LessThan($a, $b)"
    case LessThanOrEqual(a, b) =>
      q"io.getquill.ast.LessThanOrEqual($a, $b)"
    case ref: Ref =>
      q"$ref"
  }

  implicit val refLift: Liftable[Ref] = Liftable[Ref] {
    case Property(ref, name) =>
      q"io.getquill.ast.Property($ref, $name)"
    case Ident(ident) =>
      q"io.getquill.ast.Ident($ident)"
    case v: Value =>
      q"$v"
  }

  implicit val valueLift: Liftable[Value] = Liftable[Value] {
    case Constant(v) =>
      q"io.getquill.ast.Constant(${Literal(c.universe.Constant(v))})"
    case NullValue =>
      q"io.getquill.ast.NullValue"
    case Tuple(values) =>
      q"io.getquill.ast.Tuple(List(..$values))"
  }

  implicit val identLift: Liftable[Ident] = Liftable[Ident] {
    case Ident(name) =>
      q"io.getquill.ast.Ident($name)"
  }

  implicit val parametrizedLift: Liftable[Parametrized] = Liftable[Parametrized] {
    case ParametrizedQuery(params, query) =>
      q"io.getquill.ast.ParametrizedQuery(List(..$params), $query)"
    case ParametrizedExpr(params, expr) =>
      q"io.getquill.ast.ParametrizedExpr(List(..$params), $expr)"
  }
}