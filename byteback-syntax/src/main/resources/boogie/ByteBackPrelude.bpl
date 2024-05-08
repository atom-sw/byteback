// -------------------------------------------------------------------
// Heap model
// -------------------------------------------------------------------
type Reference;

const unique `null`: Reference;

const unique `void`: Reference;

function reference.is_void(r: Reference) returns (bool)
{
	r == `void`
}

type Field a;

type Store = [Reference]<a>[Field a]a;

function {:inline} store.read<a>(h: Store, r: Reference, f: Field a) returns (a)
{
	h[r][f]
}

function {:inline} store.update<a>(h: Store, r: Reference, f: Field a, v: a) returns (Store)
{
	h[r := h[r][f := v]]
}

function heap.succeeds(h1: Store, h2: Store) returns (bool);

function heap.is_good(h: Store) returns (bool);

function heap.is_anchor(h: Store) returns (bool);

function heap.allocated(Store, Reference) returns (bool);

var heap: Store where heap.is_good(heap) && heap.is_anchor(heap);

axiom (forall h1: Store, h2: Store, h3: Store ::
	{ heap.succeeds(h1, h2), heap.succeeds(h2, h3) }
	h1 != h3 ==> heap.succeeds(h1, h2) && heap.succeeds(h2, h3) ==> heap.succeeds(h1, h3));

axiom (forall h1: Store, h2: Store ::
	heap.succeeds(h1, h2) &&
	(forall r: Reference, t: Type ::
		reference.typeof(h1, r) == reference.typeof(h2, r)));

axiom (forall h1: Store, h2: Store ::
	heap.succeeds(h1, h2) &&
	(forall r: Reference ::
		{ heap.allocated(h2, r) }
		heap.allocated(h1, r) == heap.allocated(h2, r)));

axiom (forall<a> h: Store, r: Reference, f: Field a, x: a ::
	{ store.update(h, r, f, x) }
	heap.is_good(h) ==> heap.succeeds(h, store.update(h, r, f, x)));

procedure new(t: Type) returns (r: Reference, e: Reference);
	ensures r != `null`;
	ensures e == `void`;
	ensures reference.typeof(heap, r) == t;
	ensures heap.allocated(heap, r);

// -------------------------------------------------------------------
// Type model
// -------------------------------------------------------------------
type Type;

const unique Primitive: Type;

const unique Object.Type: Field Type;

type `boolean` = bool;

type `byte` = int;

type `short` = int;

type `int` = int;

type `char` = int;

type `long` = int;

type `float` = real;

type `double` = real;

function type.extends(t1: Type, t2: Type) returns (bool);
 
axiom (forall t1: Type :: type.extends(t1, t1));

axiom (forall t1: Type, t2: Type, t3: Type :: type.extends(t1, t2) && type.extends(t2, t3) ==> type.extends(t1, t3));

axiom (forall t1: Type, t2: Type :: t1 != t2 && type.extends(t1, t2) ==> !type.extends(t2, t1));

function {:inline} reference.typeof(h: Store, r: Reference) returns (Type)
{
  store.read(h, r, Object.Type)
}

function {:inline} reference.instanceof(h: Store, r: Reference, t: Type) returns (bool)
{
	type.extends(store.read(h, r, Object.Type), t)
}

axiom (forall h: Store, t: Type :: !reference.instanceof(h, `void`, t));

function type.reference(Type) returns (Reference);

function type.reference_inverse(Reference) returns (Type);

axiom (forall t: Type :: { type.reference(t) } type.reference_inverse(type.reference(t)) == t);

// -------------------------------------------------------------------
// Array model
// -------------------------------------------------------------------
type Box;

function box<a>(a) returns (Box);

function unbox<a>(Box) returns (a);

axiom (forall <a> x : a :: { box(x) } unbox(box(x)) : a == x);

function array.element(int) returns (Field Box);

function array.element_inverse<a>(Field a) returns (int);

axiom (forall i: int :: { array.element(i) } array.element_inverse(array.element(i)) == i);

function array.lengthof(r: Reference) returns (int);

axiom (forall r: Reference :: { array.lengthof(r) }
	array.lengthof(r) >= 0);

function array.type(Type) returns (Type);

function array.type_inverse(Type) returns (Type);

axiom (forall t: Type :: { array.type(t) } array.type_inverse(array.type(t)) == t);

procedure array(t: Type, l: int) returns (r: Reference, e: Reference);
	ensures r != `null`;
	ensures e == `void`;
	ensures heap.allocated(heap, r);
	ensures array.lengthof(r) == l;
	ensures reference.typeof(heap, r) == array.type(t);

// -------------------------------------------------------------------
// String model
// -------------------------------------------------------------------

procedure string(chars: Reference) returns (r: Reference, e: Reference);
	ensures r != `null`;
	ensures e == `void`;
	ensures heap.allocated(heap, r);

function string.const(id: int) returns (r: Reference);

// -------------------------------------------------------------------
// Class constants
// -------------------------------------------------------------------

function type.const(id: int) returns (r: Reference);

// -------------------------------------------------------------------
// Binary operators
// -------------------------------------------------------------------

// Used to model cmpl, cmpg, cmp
function int.cmp(i: int, j: int) returns (int);

axiom (forall x: int, y: int :: { int.cmp(x, y) }
	(int.gt(x, y) ==> eq(int.cmp(x, y), 1)));
axiom (forall x: int, y: int :: { int.cmp(x, y) }
	(int.lt(x, y) ==> eq(int.cmp(x, y), -1)));
axiom (forall x: int, y: int :: { int.cmp(x, y) }
	(eq(x, y) ==> eq(int.cmp(x, y), 0)));

function real.cmp(i: real, j: real) returns (int);

axiom (forall x: real, y: real :: { real.cmp(x, y) }
	(x > y ==> real.cmp(x, y) == 1));
axiom (forall x: real, y: real :: { real.cmp(x, y) }
	(x < y ==> real.cmp(x, y) == -1));
axiom (forall x: real, y: real :: { real.cmp(x, y) }
	(eq(x, y) ==> real.cmp(x, y) == 0));


function shl(a: int, p: int) returns (int);

function shr(a: int, p: int) returns (int);

// -------------------------------------------------------------------
// Surrogate functions
// -------------------------------------------------------------------
function {:never_pattern true} and(a: bool, b: bool) returns (bool)
{
  a && b
}

function {:never_pattern true} or(a: bool, b: bool) returns (bool)
{
  a || b
}

function {:never_pattern true} implies(a: bool, b: bool) returns (bool)
{
  a ==> b
}

function {:never_pattern true} iff(a: bool, b: bool) returns (bool)
{
  a <==> b
}

function {:never_pattern true} eq<t>(a: t, b: t) returns (bool)
{
	a == b
}

function {:never_pattern true} neq<t>(a: t, b: t) returns (bool)
{
	a != b
}

function {:never_pattern true} int.lt(a: int, b: int) returns (bool)
{
	a < b
}

function {:never_pattern true} real.lt(a: real, b: real) returns (bool)
{
	a < b
}

function {:never_pattern true} int.lte(a: int, b: int) returns (bool)
{
	a <= b
}

function {:never_pattern true} real.lte(a: real, b: real) returns (bool)
{
	a <= b
}

function {:never_pattern true} int.gt(a: int, b: int) returns (bool)
{
	a > b
}

function {:never_pattern true} real.gt(a: real, b: real) returns (bool)
{
	a > b
}

function {:never_pattern true} int.gte(a: int, b: int) returns (bool)
{
	a >= b
}

function {:never_pattern true} real.gte(a: real, b: real) returns (bool)
{
	a >= b
}

function {:never_pattern} not(a: bool) returns (bool)
{
	!a
}

// -------------------------------------------------------------------
// Casting operators
// -------------------------------------------------------------------

function boolean.to.int(a: `boolean`): `int`
{ if (a) then 1 else 0  }

function int.to.float(a: `int`): `float`
{ real(a) }

function int.to.double(a: `int`): `double`
{ real(a) }

function float.to.int(a: `float`): `int`
{ int(a) }

function double.to.int(a: `double`): `int`
{ int(a) }

// -------------------------------------------------------------------
// The program starts here
// -------------------------------------------------------------------
