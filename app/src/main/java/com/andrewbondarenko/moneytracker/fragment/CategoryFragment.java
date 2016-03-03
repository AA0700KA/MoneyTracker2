package com.andrewbondarenko.moneytracker.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.andrewbondarenko.moneytracker.adapter.CategoryAdapter;
import com.andrewbondarenko.moneytracker.R;
import com.andrewbondarenko.moneytracker.domain.Category;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

public class CategoryFragment extends Fragment {

    private ListView listView;
    private FloatingActionButton fab;

    public static final String TAG = CategoryFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_fragment, null);

        listView = (ListView) view.findViewById(R.id.category_list);

        fab = (FloatingActionButton) view.findViewById(R.id.add_category);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.add_category_dialog, null);
                final EditText editText = (EditText) view.findViewById(R.id.edit_category);
                AlertDialog.Builder builder = addCategoryDialogBuilder(view, editText);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private AlertDialog.Builder addCategoryDialogBuilder(View view, final EditText editText) {
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        Category checkCategory = new Select().from(Category.class).where("Name = ?", name).executeSingle();

                        if (checkCategory == null) {
                            new Category(name).save();
                        } else {
                            Toast.makeText(getActivity(), "Category must be unique! "
                                    + name + " already exists!", Toast.LENGTH_SHORT).show();
                        }

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                new CategoryFragment(), CategoryFragment.TAG).commit();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }


    private AlertDialog.Builder updateCategoryDialogBuilder(View view, final EditText editText, final TextView textView, final AlertDialog mainDialog) {
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        Category checkCategory = new Select().from(Category.class).where("Name = ?", name).executeSingle();

                        if (checkCategory == null) {
                            new Update(Category.class).set("Name = ?", name).where("Name = ?", textView.getText()).execute();
                        } else {
                            Toast.makeText(getActivity(), "Category must be unique! "
                                    + name + " already exists!", Toast.LENGTH_SHORT).show();
                        }

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                new CategoryFragment()).commit();
                        mainDialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        final List<Category> categories = Category.initData();
        CategoryAdapter adapter = new CategoryAdapter(getActivity(), categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        TransactionFragment.getInstance(category), TransactionFragment.TAG).commit();
            }
        });
        listView.setOnItemLongClickListener(new CategoryItemLongClickListener());
    }

    private AlertDialog.Builder deleteCategoryDialogBuilder(final TextView textView, final AlertDialog mainDialog) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Delete Category")
                .setMessage("Are you sure delete this category?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new Delete().from(Category.class).where("Name = ?", textView.getText()).execute();
                                Toast.makeText(getActivity(), textView.getText() + " category delete succefull" +
                                        "", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                        new CategoryFragment(), CategoryFragment.TAG).commit();
                                mainDialog.cancel();
                            }
                        }

                ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }

                );
    }

    private class CategoryItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            View promptView = LayoutInflater.from(getContext()).inflate(R.layout.category_long_click_layout, null);
            final ListView listSettings = (ListView) promptView.findViewById(R.id.category_settings);
            final TextView textView = (TextView) view.findViewById(R.id.categoty_text);
            final String[] settingsItems = new String[]{"Delete", "Update"};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_expandable_list_item_1, settingsItems);
            listSettings.setAdapter(arrayAdapter);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(promptView);

            final AlertDialog mainDialog = builder.create();
            mainDialog.show();

            listSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listItemsClick(settingsItems, position, textView, mainDialog);
                }
            });

            return true;
        }

        private void listItemsClick(String[] settingsItems, int position, TextView textView, AlertDialog mainDialog) {
            String item = settingsItems[position];

            AlertDialog.Builder builder;
            AlertDialog dialog;

            switch (item) {
                case "Delete":
                    builder = deleteCategoryDialogBuilder(textView, mainDialog);

                    dialog = builder.create();
                    dialog.show();
                    break;
                case "Update":
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_category_dialog, null);
                    EditText editText = (EditText) view.findViewById(R.id.edit_category);
                    builder = updateCategoryDialogBuilder(view, editText, textView, mainDialog);

                    dialog = builder.create();
                    dialog.show();
                    break;
            }

        }

    }

}

