package com.zeeyeh.archcareer.manager;

import cn.hutool.core.util.IdUtil;
import com.zeeyeh.archcareer.Career;

import java.io.File;
import java.util.*;

public class CareerManagerProvider {
    private final List<Career> careers;

    private File careerFolder;

    public CareerManagerProvider() {
        careers = new ArrayList<>();
    }

    public boolean registerCareer(String name, String title) {
        long careerId = IdUtil.getSnowflakeNextId();
        return registerCareer(careerId, name, title, new ArrayList<>());
    }

    public boolean registerCareer(long id, String name, String title) {
        return registerCareer(id, name, title, new ArrayList<>());
    }

    public boolean registerCareer(long id, String name, String title, List<String> players) {
        if (existCareer(name)) return false;
        careers.add(new Career(id, name, title, players));
        return true;
    }

    public boolean unregisterCareer(String name) {
        if (!existCareer(name)) return false;
        int i = 0;
        for (Career career : careers) {
            if (career.getName().equals(name)) {
                careers.remove(i);
                break;
            }
            i++;
        }
        return true;
    }

    public boolean existCareer(String name) {
        for (Career career : careers) {
            if (career.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Career getCareer(String name) {
        for (Career career : careers) {
            if (career.getName().equals(name)) {
                return career;
            }
        }
        return null;
    }

    public Career getCareer(long id) {
        for (Career career : careers) {
            if (career.getId() == id) {
                return career;
            }
        }
        return null;
    }

    public void clear() {
        careers.clear();
    }

    public List<Career> getCareers() {
        return careers;
    }
}
