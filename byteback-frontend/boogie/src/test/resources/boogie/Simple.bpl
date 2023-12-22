type Unit;

const unique unit : Unit;

axiom unit == unit;

function identity<T>(arg: T) returns (T) { arg }

procedure nothing<T>(arg: T) {}

procedure identity<T>(arg: T) returns (ret: T) {
    var identity: T;
    identity := arg;
    ret := identity;
}

procedure prototype<T>(arg: T) returns(ret: T);

implementation prototype<T>(arg: T) returns(ret: T) {
    ret := arg;
}
