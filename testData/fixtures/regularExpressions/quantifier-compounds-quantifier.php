<?php

    preg_match(<error descr="[EA] ( \d* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\d*)*/'</error>, '');
    preg_match(<error descr="[EA] ( \D+ )+ might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\D+)+/'</error>, '');
    preg_match(<error descr="[EA] ( \w* )+ might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\w*)+/'</error>, '');
    preg_match(<error descr="[EA] ( \W+ )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\W+)*/'</error>, '');
    preg_match(<error descr="[EA] ( \s* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\s*)*/'</error>, '');
    preg_match(<error descr="[EA] ( \S* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\S*)*/'</error>, '');

    preg_match(<error descr="[EA] ( \D* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(?:\D*)*/'</error>, '');
    preg_match(<error descr="[EA] ( \D* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\D*|0(?!1))*/'</error>, '');
    preg_match(<error descr="[EA] ( \D* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(\D*|)*/'</error>, '');
    preg_match(<error descr="[EA] ( \D* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(|\D*|)*/'</error>, '');
    preg_match(<error descr="[EA] ( \D* )* might be exploited (ReDoS, Regular Expression Denial of Service).">'/(|\D*)*/'</error>, '');

    preg_match(<error descr="[EA] \D and \W are not mutually exclusive in '\D|\W' which can be exploited (ReDoS, Regular Expression Denial of Service).">'/(\D|\W)+/'</error>, '');
    preg_match(<warning descr="[EA] '(\D|\w)' is 'greedy'. Please use '([\D\w])' instead.">'/(\D|\w)+/'</warning>, '');
    preg_match(<error descr="[EA] \d and \w are not mutually exclusive in '\d|\w' which can be exploited (ReDoS, Regular Expression Denial of Service).">'/(\d|\w)+/'</error>, '');
    preg_match(<warning descr="[EA] '(\d|\W)' is 'greedy'. Please use '([\d\W])' instead.">'/(\d|\W)+/'</warning>, '');

    preg_match('/(\D*){1,10}/', '');
    preg_match('/(\D*){1,}/', '');
    preg_match('/(\D{1,})*/', '');

    preg_match('/(\D+|\W+)*+/', '');
    preg_match('/(?>(\d+|\w+)*)/', '');