package br.com.mbecker.jagastei;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import br.com.mbecker.jagastei.adapter.GastoAdapter;
import br.com.mbecker.jagastei.db.GastoModel;
import br.com.mbecker.jagastei.db.JaGasteiDbHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ARG_PARAM_MES_SEL = "mesSel";

    private ViewPager pager;
    private ExtratoMesPagerAdapter extMesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intentCadastro = new Intent(getBaseContext(), CadastroActivity.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(intentCadastro);
            }
        });

        extMesAdapter = new ExtratoMesPagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(extMesAdapter);
    }

    public static ExtratoMesFragment newInstance(short mesSel) {
        ExtratoMesFragment fragment = new ExtratoMesFragment();
        Bundle args = new Bundle();
        args.putShort(ARG_PARAM_MES_SEL, mesSel);
        fragment.setArguments(args);
        return fragment;
    }

    public class ExtratoMesPagerAdapter extends FragmentStatePagerAdapter {

        private final short NUM_MESES = 5;

        public ExtratoMesPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return MainActivity.newInstance((short) position);
        }

        @Override
        public int getCount() {
            return NUM_MESES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "" + position;
        }
    }


    public static class ExtratoMesFragment extends Fragment {
        private short mes;
        private JaGasteiDbHelper db;
        private RecyclerView recyclerView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                db = new JaGasteiDbHelper(getContext());
                mes = getArguments().getShort(ARG_PARAM_MES_SEL);
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_blank, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            //TODO mes atual e 5 meses para trás

            String mesAtual;
            Calendar c = Util.ajustarMes(mes);
            mesAtual = legendaMes(c) + " - " + c.get(Calendar.YEAR);

            String mesAno = Util.mesAno(c);
            List<GastoModel> lst = db.listarGastos(mesAno);

            TextView mes = view.findViewById(R.id.tvMes);
            TextView total = view.findViewById(R.id.tvTotal);

            mes.setText(mesAtual);
            total.setText(Util.somarGastos(lst));

            recyclerView = view.findViewById(R.id.gastos_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            recyclerView.setAdapter(new GastoAdapter(lst));
        }

        private String legendaMes(Calendar c) {
            String mesAtual;
            if (mes == 0) {
                mesAtual = getString(R.string.mes_atual);
            } else {
                mesAtual = getResources().getStringArray(R.array.meses_array)[c.get(Calendar.MONTH)];
            }

            return mesAtual;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
