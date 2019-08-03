package com.eliaovideo.videoline.modle.custommsg;


import com.eliaovideo.videoline.LiveConstant;

public class CustomMsgVideoCallEnd extends CustomMsg
{
    public CustomMsgVideoCallEnd()
    {
        super();
        setType(LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_END);
    }

}
