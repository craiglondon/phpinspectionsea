<?php

function cases_holder($x) {
    return [
        array_merge([], $x, [], []),
        array_merge($x, []),

        $x = [0],
        array_push($x, 0),
        array_push($x, 0, 1, 2),
        $x = array_merge($x, []),
        $x['key'] = 'value',
        $x = array_merge($x, ['key' => 'value', '...' => '...']),

        $x = [],
        $x = ['...', '...' => '...'],
        $x = ['...' => '...', '...'],
        $x = array_merge( ... []),
        $x = array_merge( [], ... []),

        array_unshift($x, 0),
        array_unshift($x, 0, 1, 2),
        $x = array_merge([], $x),
        $x = array_merge([&$x], $x),
        $x = array_merge(['key' => 'value'], $x),
    ];
}