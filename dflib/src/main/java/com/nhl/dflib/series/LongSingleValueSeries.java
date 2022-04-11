package com.nhl.dflib.series;

import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;

public class LongSingleValueSeries extends LongBaseSeries {

    private final long value;
    private final int size;

    public LongSingleValueSeries(long value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public long getLong(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return value;
    }

    @Override
    public void copyToLong(long[] to, int fromOffset, int toOffset, int len) {
        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }
        int targetIdx = toOffset;
        for (int i = fromOffset; i < len; i++) {
            to[targetIdx++] = get(i);
        }
    }

    @Override
    public LongSeries materializeLong() {
        return this;
    }

    @Override
    public LongSeries rangeOpenClosedLong(int fromInclusive, int toExclusive) {
        return fromInclusive == 0 && toExclusive == size()
                ? this
                : new LongSingleValueSeries(value, toExclusive - fromInclusive);
    }

    @Override
    public LongSeries headLong(int len) {
        return len < size ? new LongSingleValueSeries(value, len) : this;
    }

    @Override
    public LongSeries tailLong(int len) {
        return len < size ? new LongSingleValueSeries(value, len) : this;
    }

    @Override
    public long max() {
        return value;
    }

    @Override
    public long min() {
        return value;
    }

    @Override
    public long sum() {
        return value * size;
    }

    @Override
    public double avg() {
        return value;
    }

    @Override
    public double median() {
        return value;
    }

    public static void main(String[] args) {
        String s = "04a037b7c436482d8518b460f07dae50\n" +
                "0bc24a9511a840ed993c15e4d3ac5111\n" +
                "22c161c7c4e334a206602fdeda900473\n" +
                "2d1b0607090c4283b87d75a87f7156b0\n" +
                "2e1ac4169d9143a193ed7bb9b45d7ed2\n" +
                "35a60e585b0c4760a7a9b7e5620f311d\n" +
                "4bec3c4ae3724d03aa4a4d6843866015\n" +
                "506f443d2fa0445fb8963c192361b446\n" +
                "62de6f89e6094c99a962d9857874c448\n" +
                "62f767caa5f44469965ce12542b187c1\n" +
                "6ecd4ad8ee4d4f838f2b483fd84562d6\n" +
                "723560a8a39c46d2a5e147cebf86704b\n" +
                "78b28eaff45a4967a25e614946499178\n" +
                "8686c091760642a697dc35379580f869\n" +
                "a64adf52f6fc45f582f2ff68beae01a2\n" +
                "b007dcef68a54df988b297448573dcfc\n" +
                "c03f932d433f4672aec1dadc614ac393\n" +
                "cb7861ecbce347bb87d11a3fabe5b387\n" +
                "e6c47072eb2b44e490e0cbc45bfb37f7\n" +
                "e87990a538404593b64c3c4751f837b4\n" +
                "e87990a538404593b64c3c4751f837b5\n" +
                "f2466f7ec13041dd814b39453e612ace\n" +
                "f319f36d54a74eedb17167e32b5753ff";
        StringBuilder sf = new StringBuilder();
        for (String s1 : s.split("\n")) {
            sf.append("\\\"" +s1 + "\\\",");
        }

        System.out.println(sf.toString());
    }
}
