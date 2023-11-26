package com.zeeyeh.archcareer.api;

import cn.hutool.core.util.IdUtil;
import com.zeeyeh.archcareer.Career;
import com.zeeyeh.archcareer.manager.CareerManagerProvider;

import java.util.ArrayList;
import java.util.List;

public class CareerManager {
    private static CareerManagerProvider careerManager;

    public static void initCareerManagerProvider(CareerManagerProvider careerManagerProvider) {
        careerManager = careerManagerProvider;
    }

    public Career getCareer(long id) {
        return careerManager.getCareer(id);
    }

    public Career getCareer(String name) {
        return careerManager.getCareer(name);
    }

    public List<Career> list() {
        return careerManager.getCareers();
    }

    public boolean remove(String name) {
        return careerManager.unregisterCareer(name);
    }

    public boolean create(String name, String title) {
        long id = IdUtil.getSnowflakeNextId();
        return create(id, name, title);
    }

    public boolean create(long id, String name, String title) {
        return create(id, name, title, new ArrayList<>());
    }

    public boolean create(long id, String name, String title, List<String> players) {
        return careerManager.registerCareer(id, name, title, players);
    }

    public void clear() {
        careerManager.clear();
    }
}
