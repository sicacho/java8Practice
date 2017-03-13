package com.company.downloadlink;

import java.util.List;

/**
 * Created by Administrator on 8/25/2016.
 */
public class LinkCommentDTO {
    public String code_video;
    public String linkprimary;
    public String title;
    public List<String> uploaded_net;
    public List<String> rapidgator_net;
    public Integer numberWantIt = 0;
    public String create_date;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkCommentDTO that = (LinkCommentDTO) o;

        return code_video.equals(that.code_video);

    }

    @Override
    public int hashCode() {
        return code_video.hashCode();
    }
}
