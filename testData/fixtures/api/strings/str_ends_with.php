<?php

return [
    <weak_warning descr="[EA] Can be replaced by 'str_ends_with('haystack', 'needle')' (improves maintainability).">strpos(strrev('haystack'), strrev('needle')) === 0</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by 'str_ends_with('haystack', 'needle')' (improves maintainability).">strpos('haystack', 'needle', -strlen('needle')) !== -1</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by 'str_ends_with('haystack', 'needle')' (improves maintainability).">substr('haystack', -6) === 'needle'</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by 'str_ends_with('haystack', 'needle')' (improves maintainability).">substr('haystack', -strlen('needle')) === 'needle'</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by 'str_ends_with('haystack', 'needle')' (improves maintainability).">mb_substr('haystack', - mb_strlen('needle')) === 'needle'</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by 'str_ends_with('haystack', 'needle')' (improves maintainability).">substr_compare('haystack', 'needle', -strlen('needle')) === 0</weak_warning>,

    <weak_warning descr="[EA] Can be replaced by '! str_ends_with('haystack', 'needle')' (improves maintainability).">strpos(strrev('haystack'), strrev('needle')) !== 0</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by '! str_ends_with('haystack', 'needle')' (improves maintainability).">strpos('haystack', 'needle', -strlen('needle')) === -1</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by '! str_ends_with('haystack', 'needle')' (improves maintainability).">substr('haystack', -6) !== 'needle'</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by '! str_ends_with('haystack', 'needle')' (improves maintainability).">substr('haystack', -strlen('needle')) !== 'needle'</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by '! str_ends_with('haystack', 'needle')' (improves maintainability).">mb_substr('haystack', - mb_strlen('needle')) !== 'needle'</weak_warning>,
    <weak_warning descr="[EA] Can be replaced by '! str_ends_with('haystack', 'needle')' (improves maintainability).">substr_compare('haystack', 'needle', -strlen('needle')) !== 0</weak_warning>,

    substr('haystack', -strlen('needle')) !== '...',
    substr('haystack', -strlen('needle'), 1) !== 'needle',
    mb_substr('haystack', - mb_strlen('needle', '...')) === 'needle',
];