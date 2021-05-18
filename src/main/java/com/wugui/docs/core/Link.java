package com.wugui.docs.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Link {
    private String name;
    private String url;

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
