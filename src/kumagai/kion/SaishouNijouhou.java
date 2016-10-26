package kumagai.kion;

import java.util.*;
import java.awt.geom.*;

/**
 * 最小2乗法の数値計算例(n次多項式で近似)(C言語)
 * http://www.geocities.jp/supermisosan/saisyounizyouhou2.html
 */
public class SaishouNijouhou
{
	static int CHECK = 0; //ガウスの消去法における三角行列のチェック用

	/**
	 * 最小2乗法の数値計算(n次多項式で近似)
	 * @param x 対象X軸の値
	 * @param y 対象Y軸の値
	 * @param n n-1次の多項式で近似（１以上）
	 * @param N ガウスの消去法における未知数の数（上のnと同じ値にすること）
	 * @param S データの個数
	 * @return 近似値リスト
	 */
	static public ArrayList<Point2D.Double> getKinji
		(double x[], double y[], int n, int N,int S)
	{
		int i,j,k;
		double X,Y;
		double [][]A;
		double []xx;

		A = new double [n][];

		for (i=0 ; i<n ; i++)
		{
			A[i] = new double [n + 1];
		}

		xx = new double [N];

		/*初期化*/
		for(i=0;i<n;i++)
		{
			for(j=0;j<n+1;j++)
			{
				A[i][j]=0.0;
			}
		}

		/*ガウスの消去法で解く行列の作成*/
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				for(k=0;k<S;k++)
				{
					A[i][j] += Math.pow(x[k], i+j);
				}
			}
		}
		for(i=0;i<n;i++)
		{
			for(k=0;k<S;k++)
			{
				A[i][n] += Math.pow(x[k], i) * y[k];
			}
		}

		// ガウスの消去法の実行（配列xxは解、すなわち多項式の係数を入れるための
		// もの）
		int i1,j1,k1,l,pivot;
		double x1[];
		double p,q,m;
		double [][]b;

		x1 = new double [N];

		b = new double [1][];

		for (i1=0 ; i1<1 ; i1++)
		{
			b[i1] = new double [N + 1];
		}

		for(i1=0;i1<N;i1++)
		{
			m=0;
			pivot=i1;

			for(l=i1;l<N;l++)
			{
				if (Math.abs(A[l][i1])>m)
				{
					//i列の中で一番値が大きい行を選ぶ
					m=Math.abs(A[l][i1]);
					pivot=l;
				}
			}

			if (pivot!=i1)
			{
				// pivotがiと違えば、行の入れ替え

				for (j1=0 ; j1<N+1 ; j1++)
				{
					b[0][j1] = A[i1][j1];
					A[i1][j1] = A[pivot][j1];
					A[pivot][j1] = b[0][j1];
				}
			}
		}

		for(k1=0;k1<N;k1++)
		{
			p=A[k1][k1];	      //対角要素を保存
			A[k1][k1]=1;	      //対角要素は１になることがわかっているから

			for(j1=k1+1;j1<N+1;j1++)
			{
				A[k1][j1]/=p;
			}

			for(i1=k1+1;i1<N;i1++)
			{
				q=A[i1][k1];

				for(j1=k1+1;j1<N+1;j1++)
				{
					A[i1][j1]-=q*A[k1][j1];
				}

				A[i1][k1] = 0; //０となることがわかっているところ
			}
		}

		//解の計算
		for(i1=N-1;i1>=0;i1--)
		{
			x1[i1] = A[i1][N];

			for(j1=N-1;j1>i1;j1--)
			{
				x1[i1] -= A[i1][j1]*x1[j1];
			}
		}

		//行列が最後どうなったか見たいときに実行
		if (CHECK==1)
		{
			for(i1=0;i1<N;i1++)
			{
				for(j1=0;j1<N+1;j1++)
				{
					System.out.printf("%10.3f",A[i1][j1]);
				}

				System.out.printf("\n");
			}
		}

		//System.out.printf("解は\n");
		for(i1=0 ; i1<N ; i1++)
		{
			//System.out.printf("%f\n",x[i]);
			xx[i1] = x1[i1];
		}

		/* 最小２乗法による関数のデータを生成 */
		ArrayList<Point2D.Double> list = new ArrayList<Point2D.Double>();

		for (X=x[0] ; X<x[S-1] ; X+=1)
		{
			Y = 0.0;

			for (i=0 ; i<N ; i++)
			{
				Y += xx[i] * Math.pow(X, i);
			}

			list.add(new Point2D.Double(X, Y));
		}

		return list;
	}
}
