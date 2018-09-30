package remix.myplayer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import remix.myplayer.Global;
import remix.myplayer.R;
import remix.myplayer.bean.mp3.PlayListSong;
import remix.myplayer.bean.mp3.Song;
import remix.myplayer.helper.MusicServiceRemote;
import remix.myplayer.service.Command;
import remix.myplayer.service.MusicService;
import remix.myplayer.theme.ThemeStore;
import remix.myplayer.ui.adapter.holder.BaseViewHolder;
import remix.myplayer.util.MediaStoreUtil;
import remix.myplayer.util.PlayListUtil;
import remix.myplayer.util.Util;

/**
 * Created by Remix on 2015/12/2.
 */

/**
 * 正在播放列表的适配器
 */
public class PlayQueueAdapter extends BaseAdapter<PlayListSong, PlayQueueAdapter.PlayQueueHolder> {
    private int mAccentColor;
    private int mTextColor;

    public PlayQueueAdapter(Context context, int layoutId) {
        super(context, layoutId);
        mAccentColor = ThemeStore.getAccentColor();
        mTextColor = ThemeStore.getTextColorPrimary();
    }

    @Override
    protected void convert(final PlayQueueHolder holder, PlayListSong playListSong, int position) {
        final int audioId = playListSong.AudioId;
        final Song item = MediaStoreUtil.getMP3InfoById(audioId);
        if (item == null) {
            //歌曲已经失效
            holder.mSong.setText(mContext.getString(R.string.song_lose_effect));
            holder.mArtist.setVisibility(View.GONE);
        } else {
            //设置歌曲与艺术家
            holder.mSong.setText(item.getTitle());
            holder.mArtist.setText(item.getArtist());
            holder.mArtist.setVisibility(View.VISIBLE);
//                //高亮
            if (MusicServiceRemote.getCurrentSong().getId() == item.getId()) {
                holder.mSong.setTextColor(mAccentColor);
            } else {
//                holder.mSong.setTextColor(Color.parseColor(ThemeStore.isDay() ? "#323335" : "#ffffff"));
                holder.mSong.setTextColor(mTextColor);
            }
        }

        //删除按钮
        holder.mDelete.setOnClickListener(v -> {
            if (PlayListUtil.deleteSong(audioId, Global.PlayQueueID)) {
                if (MusicServiceRemote.getCurrentSong().getId() == audioId) {
                    Util.sendLocalBroadcast(new Intent(MusicService.ACTION_CMD).putExtra("Control", Command.NEXT));
                }
            }

        });
        if (mOnItemClickLitener != null) {
            holder.mContainer.setOnClickListener(v -> mOnItemClickLitener.onItemClick(v, holder.getAdapterPosition()));
        }
    }

    static class PlayQueueHolder extends BaseViewHolder {
        @BindView(R.id.playlist_item_name)
        TextView mSong;
        @BindView(R.id.playlist_item_artist)
        TextView mArtist;
        @BindView(R.id.playqueue_delete)
        ImageView mDelete;
        @BindView(R.id.item_root)
        RelativeLayout mContainer;

        public PlayQueueHolder(View v) {
            super(v);
        }
    }
}
