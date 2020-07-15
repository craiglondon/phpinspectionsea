<?php

class CasesHolder {

    public function ternaries($x, $y, $z) {
        /* pattern: can be simplified to a condition operand */
        ($x === $y) ? $x : <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$y</warning>;

        $x === $y ? $x : <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$y</warning>;
        $x !== $y ? <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$x</warning> : $y;
        $x == $y ? $x : <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$y</warning>;
        $x != $y ? <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$x</warning> : $y;

        $x === 0 ? 0 : <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$x</warning>;
        $x !== 0 ? <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$x</warning> : 0;
        $x === 0 ? $x : <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">0</warning>;
        $x !== 0 ? <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">0</warning> : $x;

        /* pattern: identical branches */
        $x ? $y : <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct.">$y</warning>;

        /* false-positives */
        $x === $y ? $x : $z;
        $x !== $y ? $x : $z;
        $x >= $y ? $x : $y;
        $x ? $x : $y;
    }

    public function conditionals($x, $y) {
        if ($x === $y) {
            return $x;
        } else {
            return <warning descr="[EA] Actually the same value gets returned by the alternative return. It's possible to simplify the construct.">$y</warning>;
        }

        if ($x !== $y) {
            return <warning descr="[EA] Actually the same value gets returned by the alternative return. It's possible to simplify the construct.">$x</warning>;
        } else {
            return $y;
        }

        if ($x === $y) {
            return $x;
        }
        return <warning descr="[EA] Actually the same value gets returned by the alternative return. It's possible to simplify the construct.">$y</warning>;

        if ($x !== $y) {
            return <warning descr="[EA] Actually the same value gets returned by the alternative return. It's possible to simplify the construct.">$x</warning>;
        }
        return $y;

        if ($x === $y) {
            return $x;
        } else {
            return <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct."><warning descr="[EA] Same value gets returned by the alternative return. It's possible to simplify the construct.">$x</warning></warning>;
        }

        if ($x === $y) {
            return $x;
        }
        return <warning descr="[EA] Actually the same value is in the alternative variant. It's possible to simplify the construct."><warning descr="[EA] Same value gets returned by the alternative return. It's possible to simplify the construct.">$x</warning></warning>;


        /* false-positives */
        if ($x === $y) {
            $this->ternaries($x, $y);
            return $x;
        } else {
            return $y;
        }

        if ($x === $y) {
            $this->ternaries($x, $y);
            return $x;
        }
        return $y;
    }

    public function falsy($parameter) {
        if ($parameter == null) {
            return null;
        } else {
            return $parameter;
        }

        if ($parameter != null) {
            return $parameter;
        }
        return null;
    }
}