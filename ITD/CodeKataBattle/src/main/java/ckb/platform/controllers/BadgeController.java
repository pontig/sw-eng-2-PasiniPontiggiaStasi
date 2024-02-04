package ckb.platform.controllers;

import ckb.platform.repositories.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class BadgeController {
    @Autowired
    private final BadgeRepository repository;

    BadgeController(BadgeRepository repository) {
        this.repository = repository;
    }

}
