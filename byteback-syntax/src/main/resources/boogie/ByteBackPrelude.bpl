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
		heap.typeof(h1, r) == heap.typeof(h2, r)));

axiom (forall h1: Store, h2: Store ::
	heap.succeeds(h1, h2) &&
	(forall r: Reference ::
		{ heap.allocated(h2, r) }
		heap.allocated(h1, r) == heap.allocated(h2, r)));

procedure new(t: Type) returns (r: Reference, e: Reference);
	ensures r != `null`;
	ensures e == `void`;
	ensures heap.typeof(heap, r) == t;
	ensures heap.allocated(heap, r);

// -------------------------------------------------------------------
// Type model
// -------------------------------------------------------------------
type Type;

const unique Primitive: Type;

const unique Object.Type: Field Type;

type `boolean` = int;

type `byte` = int;

type `short` = int;

type `int` = int;

type `long` = int;

type `float` = real;

type `double` = real;

function type.extends(t1: Type, t2: Type) returns (bool);
 
axiom (forall t1: Type :: type.extends(t1, t1));

axiom (forall t1: Type, t2: Type, t3: Type :: type.extends(t1, t2) && type.extends(t2, t3) ==> type.extends(t1, t3));

axiom (forall t1: Type, t2: Type :: t1 != t2 && type.extends(t1, t2) ==> !type.extends(t2, t1));

function {:inline} heap.typeof(h: Store, r: Reference) returns (Type)
{
  store.read(h, r, Object.Type)
}

function {:inline} heap.instanceof(h: Store, r: Reference, t: Type) returns (bool)
{
	type.extends(store.read(h, r, Object.Type), t)
}

axiom (forall h: Store, t: Type :: !heap.instanceof(h, `void`, t));

function type.reference(Type) returns (Reference);

function type.reference_inverse(Reference) returns (Type);

axiom (forall t: Type :: { type.reference(t) } type.reference_inverse(type.reference(t)) == t);

// -------------------------------------------------------------------
// Array model
// -------------------------------------------------------------------
type Box;

function box<a>(a) returns (Box);

function unbox<a>(Box) returns (a);

axiom (forall <a> x : a :: { box(x) } unbox(box(x)) == x);

function array.element(int) returns (Field Box);

function array.element_inverse<a>(Field a) returns (int);

axiom (forall i: int :: { array.element(i) } array.element_inverse(array.element(i)) == i);

function array.lengthof(r: Reference) returns (int);

axiom (forall r: Reference :: array.lengthof(r) >= 0);

axiom (forall h1: Store, h2: Store, r: Reference , i: int ::
	heap.succeeds(h1, h2) && 0 <= i && i < array.lengthof(r)
	==> store.read(h1, r, element(i)) == store.read(h2, r, element(i)));

function array.type(Type) returns (Type);

function array.type_inverse(Type) returns (Type);

axiom (forall t: Type :: { array.type(t) } array.type_inverse(array.type(t)) == t);

function {:inline} array.read<b>(h: Store, a: Reference, i: int) returns (b)
{ unbox(store.read(h, a, element(i))) : b }

function {:inline} array.update<b>(h: Store, a: Reference, i: int, v: b) returns (Store)
{ store.update(h, a, element(i), box(v)) }

procedure array(t: Type, l: int) returns (r: Reference, e: Reference);
	ensures r != `null`;
	ensures e == `void`;
	ensures heap.allocated(heap, r);
	ensures array.lengthof(r) == l && array.lengthof(r) >= 0;
	ensures heap.typeof(heap, r) == array.type(t);

// -------------------------------------------------------------------
// String model
// -------------------------------------------------------------------

procedure string(chars: Reference) returns (r: Reference, e: Reference);
	ensures r != `null`;
	ensures e == `void`;
	ensures heap.allocated(heap, r);

function string.const(id: int) returns (r: Reference);

// -------------------------------------------------------------------
// Binary operators
// -------------------------------------------------------------------

// Used to model cmpl, cmpg, cmp
function int.cmp(i: int, j: int) returns (int)
{
  if (i == j) then 0 else (if (i > j) then 1 else -1)
}

function real.cmp(i: real, j: real) returns (int)
{
  if (i == j) then 0 else (if (i > j) then 1 else -1)
}

function shl(a: int, p: int) returns (int);

function shr(a: int, p: int) returns (int);

// -------------------------------------------------------------------
// Surrogate functions
// -------------------------------------------------------------------
function and(a: bool, b: bool) returns (bool)
{
  a && b
}

function or(a: bool, b: bool) returns (bool)
{
  a || b
}

function implies(a: bool, b: bool) returns (bool)
{
  a ==> b
}

function iff(a: bool, b: bool) returns (bool)
{
  a <==> b
}

function eq<t>(a: t, b: t) returns (bool)
{
	a == b
}

function neq<t>(a: t, b: t) returns (bool)
{
	a != b
}

function int.lt(a: int, b: int) returns (bool)
{
	a < b
}

function real.lt(a: real, b: real) returns (bool)
{
	a < b
}

function int.lte(a: int, b: int) returns (bool)
{
	a <= b
}

function real.lte(a: real, b: real) returns (bool)
{
	a <= b
}

function int.gt(a: int, b: int) returns (bool)
{
	a > b
}

function real.gt(a: real, b: real) returns (bool)
{
	a > b
}

function int.gte(a: int, b: int) returns (bool)
{
	a >= b
}

function real.gte(a: real, b: real) returns (bool)
{
	a >= b
}

function not(a: bool) returns (bool)
{
	!a
}

// -------------------------------------------------------------------
// The program starts here
// -------------------------------------------------------------------
